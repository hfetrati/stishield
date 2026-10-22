# STI Shield Backend

Serverless backend for the STI Shield Android app. The OpenAI key stays in
Secrets Manager and never ships in the APK.

## API

All request and response bodies are JSON. The API is unauthenticated; see
[Limitations](#limitations).

| Method | Path | Body | Response |
|---|---|---|---|
| `POST` | `/chat` | `{ persona, message, previousResponseId? }` | `{ message, responseId }` |
| `POST` | `/users/register` | `{ email, nickname }` | `{ message }` |
| `POST` | `/users/points` | `{ email, points }` | `{ message }` |
| `POST` | `/users/quiz-score` | `{ email, difficulty, score }` | `{ message }` |
| `POST` | `/users/usage-time` | `{ email, usageTime }` (ms) | `{ message }` |
| `GET` | `/leaderboard` | – | `[{ rank, nickname, points, email }]` |

`persona` is `"obinna"` or `"hana"`. Each persona maps to a stored prompt id in
the secret, so the system prompt can be edited in the OpenAI dashboard without
redeploying. The full prompt text is in
[prompts/safe-talk-prompt.md](prompts/safe-talk-prompt.md). `previousResponseId` is the `responseId` from the prior turn; the
client sends it to continue a conversation and drops it to start a new one, so
the Lambda holds no conversation state.

## Data model

One DynamoDB table with partition key `email`. Its generated name is shown in
the `UsersTableName` deploy output.

```json
{
  "email": "user@example.com",
  "nickname": "Ada",
  "registrationDateTime": "May 13, 2026, 10:42:07 a.m.",
  "points": 450,
  "quizScores": ["easy--May 13, 2026, 10:50:01 a.m.--8"],
  "usageTime": { "time": 37, "lastUpdated": "May 14, 2026, 09:12:44 p.m." }
}
```

## Deploy

Prerequisites: an AWS account, the [AWS CLI](https://aws.amazon.com/cli/) configured with deployment permissions, the [SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html), and local Node.js 22 and npm installed for `sam build`.

```bash
cd backend
sam build
sam deploy --guided
```

Enter a stack name unique to your AWS account and region. AWS generates the API URL and resource names,
including the table and secret. After deployment, copy the `ApiUrl` output into
`local.properties` as `AWS_URL`, and fill in the secret identified by
`OpenAISecretArn`:

```json
{
  "OPENAI_API_KEY": "sk-...",
  "OBINNA_PROMPT_ID": "pmpt_...",
  "HANA_PROMPT_ID": "pmpt_..."
}
```

The prompt ids come from the stored prompts in the OpenAI dashboard, using
[prompts/safe-talk-prompt.md](prompts/safe-talk-prompt.md).

Deleting the stack deletes its DynamoDB table, user data, and OpenAI secret.

## Limitations

- **No authentication.** The API does not check who is calling it. The app
  simply includes the user's email in each request, and the backend trusts
  it. That means anyone who knows the API URL could send a request with
  someone else's email and change that person's points. A production version
  would require users to sign in (for example with Amazon Cognito) and would
  take the user's identity from their sign-in token rather than from the
  request body.
- **The leaderboard reads the whole table.** To build the leaderboard, the
  backend fetches every user record and sorts them in memory. This is quick
  for a study with a limited number of users, but it gets slower and more expensive
  as the table grows. A fix could be to recompute the leaderboard on a schedule and store the result,
  so each request reads only the top entries.
- **No per-user limit on chat requests.** Every message sent to `/chat` is
  forwarded to OpenAI, and each one costs money. API Gateway throttles the
  route at a target rate of 5 requests per second with a burst limit of 10. The limit
  is shared by everyone, and nothing stops one user from
  sending many messages slowly over a day. A per-user daily cap would need
  the backend to know who is calling, so it depends on adding authentication
  first.
