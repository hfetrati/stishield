// Safe Talk chat handler.
//
// Receives a user message plus a persona id from the Android client, looks up
// the OpenAI API key and the persona's stored prompt id in Secrets Manager, and
// calls the OpenAI Responses API. Conversation state is carried by
// `previous_response_id`, so the Lambda itself is stateless and the API key
// never leaves AWS.

const SECRET_ID = process.env.OPENAI_SECRET_ID?.trim();
const OPENAI_MODEL = process.env.OPENAI_MODEL?.trim();
const OPENAI_RESPONSES_URL = "https://api.openai.com/v1/responses";

let cachedSecret = null;

async function getSecret(abortSignal) {
  if (cachedSecret) return cachedSecret;

  const { SecretsManagerClient, GetSecretValueCommand } = await import(
    "@aws-sdk/client-secrets-manager"
  );

  const client = new SecretsManagerClient({});
  const result = await client.send(
    new GetSecretValueCommand({ SecretId: SECRET_ID }),
    { abortSignal }
  );

  cachedSecret = JSON.parse(result.SecretString);
  return cachedSecret;
}

function extractOutputText(data) {
  if (data.output_text) return data.output_text;

  let text = "";

  for (const item of data.output || []) {
    if (item.type === "message") {
      for (const content of item.content || []) {
        if (content.type === "output_text") {
          text += content.text;
        }
      }
    }
  }

  return text.trim();
}

function response(statusCode, body) {
  return {
    statusCode,
    headers: {
      "Content-Type": "application/json",
      "Access-Control-Allow-Origin": "*",
    },
    body: JSON.stringify(body),
  };
}

export const handler = async (event) => {
  // Leave time to return an error before the Lambda and API deadlines.
  const requestSignal = AbortSignal.timeout(25_000);
  try {
    if (!SECRET_ID || !OPENAI_MODEL) {
      console.error("OPENAI_SECRET_ID and OPENAI_MODEL must be configured");
      return response(503, { error: "Chat service is not configured" });
    }

    const body = JSON.parse(event.body || "{}");
    const { persona, message, previousResponseId } = body;

    if (!message) {
      return response(400, { error: "Missing message" });
    }

    const secret = await getSecret(requestSignal);

    const promptId =
      persona === "obinna" ? secret.OBINNA_PROMPT_ID : secret.HANA_PROMPT_ID;

    const openAiBody = {
      model: OPENAI_MODEL,
      store: true,
      prompt: { id: promptId },
      input: [{ role: "user", content: message }],
      tools: [{ type: "web_search" }],
    };

    if (previousResponseId) {
      openAiBody.previous_response_id = previousResponseId;
    }

    const openAiResponse = await fetch(OPENAI_RESPONSES_URL, {
      method: "POST",
      signal: requestSignal,
      headers: {
        Authorization: `Bearer ${secret.OPENAI_API_KEY}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(openAiBody),
    });

    const data = await openAiResponse.json();

    if (!openAiResponse.ok) {
      console.error("OpenAI error", data);
      return response(openAiResponse.status, {
        error: "OpenAI request failed",
      });
    }

    return response(200, {
      message: extractOutputText(data),
      responseId: data.id,
    });
  } catch (err) {
    console.error(err);
    if (requestSignal.aborted) {
      return response(504, { error: "Chat request timed out. Please try again." });
    }
    return response(500, { error: "Server error" });
  }
};
