import type { AxiosResponse, AxiosError } from "axios";
import Axios from "axios";
import { TodoistRequestError } from "./types/errors";
import type { HttpMethod } from "./types/http";
import { v4 as uuidv4 } from "uuid";
import axiosRetry from "axios-retry";
import { API_SYNC_BASE_URI } from "./consts/endpoints";

function paramsSerializer(params: Record<string, unknown>): string {
  return new URLSearchParams(
    Object.entries(params).reduce((acc, [key, value]) => {
      acc[key] = Array.isArray(value) ? value.join(",") : String(value);
      return acc;
    }, {} as Record<string, string>)
  ).toString();
}

const defaultHeaders = {
  "Content-Type": "application/json",
};

function getAuthHeader(apiKey: string) {
  return `Bearer ${apiKey}`;
}

function isNetworkError(error: AxiosError) {
  return Boolean(!error.response && error.code !== "ECONNABORTED");
}

function getRetryDelay(retryCount: number) {
  return retryCount === 1 ? 0 : 500;
}

function isAxiosError(error: unknown): error is AxiosError {
  return Boolean((error as AxiosError)?.isAxiosError);
}

function getTodoistRequestError(
  error: Error | AxiosError,
  originalStack?: Error
): TodoistRequestError {
  const requestError = new TodoistRequestError(error.message);

  requestError.stack =
    isAxiosError(error) && originalStack ? originalStack.stack : error.stack;

  if (isAxiosError(error) && error.response) {
    requestError.httpStatusCode = error.response.status;
    requestError.responseData = error.response.data;
  }

  return requestError;
}

function getRequestConfiguration(
  baseURL: string,
  apiToken?: string,
  requestId?: string
) {
  const authHeader = apiToken
    ? { Authorization: getAuthHeader(apiToken) }
    : undefined;
  const requestIdHeader = requestId ? { "X-Request-Id": requestId } : undefined;
  const headers = { ...defaultHeaders, ...authHeader, ...requestIdHeader };

  return { baseURL, headers };
}

function getAxiosClient(
  baseURL: string,
  apiToken?: string,
  requestId?: string
) {
  const configuration = getRequestConfiguration(baseURL, apiToken, requestId);
  const client = Axios.create(configuration);

  axiosRetry(client, {
    retries: 3,
    retryCondition: isNetworkError,
    retryDelay: getRetryDelay,
  });

  return client;
}

export function isSuccess(response: AxiosResponse): boolean {
  return response.status >= 200 && response.status < 300;
}

export async function request<T>(
  httpMethod: HttpMethod,
  baseUri: string,
  relativePath: string,
  /*apiToken?: string,*/
  payload?: Record<string, unknown>,
  requestId?: string
): Promise<AxiosResponse<T>> {
  const originalStack = new Error();

  try {
    if (
      httpMethod === "POST" &&
      !requestId &&
      !baseUri.includes(API_SYNC_BASE_URI)
    ) {
      requestId = uuidv4();
    }

    const axiosClient = getAxiosClient(baseUri, undefined, requestId);

    switch (httpMethod) {
      case "GET":
        return await axiosClient.get<T>(relativePath, {
          params: payload,
          paramsSerializer: { serialize: paramsSerializer },
        });
      case "POST":
        return await axiosClient.post<T>(relativePath, payload);
      case "DELETE":
        return await axiosClient.delete<T>(relativePath);
      default:
        throw new Error(`Unsupported HTTP method: ${httpMethod}`);
    }
  } catch (error: unknown) {
    if (isAxiosError(error)) {
      console.error(
        `Request failed with status ${error.response?.status}: ${error.message}`
      );
      throw getTodoistRequestError(error, originalStack);
    } else if (error instanceof Error) {
      console.error(`An error occurred: ${error.message}`);
      throw getTodoistRequestError(error, originalStack);
    } else {
      console.error("An unknown error occurred during the request");
      throw new Error("Unknown error occurred");
    }
  }
}
