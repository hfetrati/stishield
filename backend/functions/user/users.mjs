// User data handler.
//
// Single Lambda that serves every non-chat route of the HTTP API. It routes on
// path + method and persists everything in one DynamoDB table keyed by email.

import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import {
  DynamoDBDocumentClient,
  PutCommand,
  UpdateCommand,
  ScanCommand,
} from "@aws-sdk/lib-dynamodb";

const TABLE_NAME = process.env.TABLE_NAME || "STIShieldUsers";
const DISPLAY_TIME_ZONE = process.env.DISPLAY_TIME_ZONE || "America/Halifax";

const dynamoClient = new DynamoDBClient({});
const docClient = DynamoDBDocumentClient.from(dynamoClient);

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

function currentDisplayTime() {
  return new Date().toLocaleString("en-CA", {
    timeZone: DISPLAY_TIME_ZONE,
    year: "numeric",
    month: "long",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
}

async function registerNewUser(body) {
  const { email, nickname } = body;

  if (!email || !nickname) {
    return response(400, { error: "Missing email or nickname" });
  }

  await docClient.send(
    new PutCommand({
      TableName: TABLE_NAME,
      Item: {
        email,
        nickname,
        registrationDateTime: currentDisplayTime(),
        points: 0,
        quizScores: [],
      },
    })
  );

  return response(200, { message: "User registered" });
}

async function syncUserPoints(body) {
  const { email, points } = body;

  if (!email || points === undefined) {
    return response(400, { error: "Missing email or points" });
  }

  await docClient.send(
    new UpdateCommand({
      TableName: TABLE_NAME,
      Key: { email },
      UpdateExpression: "SET points = :points",
      ExpressionAttributeValues: { ":points": points },
    })
  );

  return response(200, { message: "Points synced" });
}

async function getLeaderboard() {
  const result = await docClient.send(
    new ScanCommand({ TableName: TABLE_NAME })
  );

  const users = (result.Items || [])
    .filter((user) => (user.points || 0) > 0)
    .sort((a, b) => b.points - a.points);

  // Competition ranking: equal points share a rank, next rank skips.
  let lastPoints = null;
  let lastRank = 0;

  const leaderboard = users.map((user, index) => {
    const rank = user.points === lastPoints ? lastRank : index + 1;
    lastPoints = user.points;
    lastRank = rank;

    return {
      rank,
      nickname: user.nickname || "",
      points: user.points || 0,
      email: user.email || "",
    };
  });

  return response(200, leaderboard);
}

async function recordQuizScore(body) {
  const { email, difficulty, score } = body;

  if (!email || !difficulty || score === undefined) {
    return response(400, { error: "Missing email, difficulty, or score" });
  }

  const scoreEntry = `${difficulty}--${currentDisplayTime()}--${score}`;

  await docClient.send(
    new UpdateCommand({
      TableName: TABLE_NAME,
      Key: { email },
      UpdateExpression:
        "SET quizScores = list_append(if_not_exists(quizScores, :empty), :score)",
      ExpressionAttributeValues: {
        ":empty": [],
        ":score": [scoreEntry],
      },
    })
  );

  return response(200, { message: "Quiz score recorded" });
}

async function syncUsageTime(body) {
  const { email, usageTime } = body;

  if (!email || usageTime === undefined) {
    return response(400, { error: "Missing email or usageTime" });
  }

  const usageTimeInMinutes = Math.floor(usageTime / 60000);

  await docClient.send(
    new UpdateCommand({
      TableName: TABLE_NAME,
      Key: { email },
      UpdateExpression: "SET usageTime = :usageTime",
      ExpressionAttributeValues: {
        ":usageTime": {
          time: usageTimeInMinutes,
          lastUpdated: currentDisplayTime(),
        },
      },
    })
  );

  return response(200, { message: "Usage time synced" });
}

export const handler = async (event) => {
  try {
    const path = event.rawPath || event.path;
    const method = event.requestContext?.http?.method || event.httpMethod;
    const body = event.body ? JSON.parse(event.body) : {};

    if (method === "POST" && path === "/users/register") {
      return await registerNewUser(body);
    }
    if (method === "POST" && path === "/users/points") {
      return await syncUserPoints(body);
    }
    if (method === "GET" && path === "/leaderboard") {
      return await getLeaderboard();
    }
    if (method === "POST" && path === "/users/quiz-score") {
      return await recordQuizScore(body);
    }
    if (method === "POST" && path === "/users/usage-time") {
      return await syncUsageTime(body);
    }

    return response(404, { error: "Route not found" });
  } catch (err) {
    console.error(err);
    return response(500, { error: "Server error" });
  }
};
