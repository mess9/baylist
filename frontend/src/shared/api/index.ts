import type {
  GetProjectDataArgs,
  GetProjectDataResponse,
  ReorderItemsArgs,
  ReorderSectionsArgs,
  MoveItemArgs,
  AddItemArgs,
  UpdateItemArgs,
  DeleteItemArgs,
  SyncResponseWithCommand,
  SyncRequest,
  SyncResponse,
  UpdateSectionArgs,
} from "./types/sync";
import { request } from "./restClient";

import {
  getRestBaseUri,
  getSyncBaseUri,
  ENDPOINT_SYNC_GET_PROJECT_DATA,
  ENDPOINT_SYNC_REORDER_SECTIONS,
  ENDPOINT_SYNC_ADD_ITEMS,
  ENDPOINT_SYNC_UPDATE_ITEMS,
  ENDPOINT_SYNC_REORDER_ITEMS,
  ENDPOINT_SYNC_MOVE_ITEMS,
  ENDPOINT_SYNC_DELETE_ITEMS,
  ENDPOINT_SYNC,
  ENDPOINT_SYNC_UPDATE_SECTIONS,
} from "./consts/endpoints";

export class TodoistApi {
  // authToken?: string;

  constructor(authToken?: string, baseUrl?: string) {
    // this.authToken = authToken;

    this.restApiBase = getRestBaseUri(baseUrl);
    this.syncApiBase = getSyncBaseUri();
  }

  private restApiBase: string;
  private syncApiBase: string;

  async getProjectData(
    args: GetProjectDataArgs,
  ): Promise<GetProjectDataResponse> {
    const response = await request<GetProjectDataResponse>(
      "GET",
      this.syncApiBase,
      ENDPOINT_SYNC_GET_PROJECT_DATA,
      // this.authToken,
      args,
    );

    return response.data;
  }

  async addItem(args: AddItemArgs): Promise<SyncResponseWithCommand> {
    const response = await request<SyncResponseWithCommand>(
      "POST",
      this.syncApiBase,
      ENDPOINT_SYNC_ADD_ITEMS,
      // this.authToken,
      args,
    );

    return response.data;
  }

  async updateItem(args: UpdateItemArgs): Promise<SyncResponseWithCommand> {
    const response = await request<SyncResponseWithCommand>(
      "POST",
      this.syncApiBase,
      ENDPOINT_SYNC_UPDATE_ITEMS,
      // this.authToken,
      args,
    );

    return response.data;
  }

  async moveItem(args: MoveItemArgs): Promise<SyncResponseWithCommand> {
    const response = await request<SyncResponseWithCommand>(
      "POST",
      this.syncApiBase,
      ENDPOINT_SYNC_MOVE_ITEMS,
      // this.authToken,
      args,
    );

    return response.data;
  }

  async deleteItem(args: DeleteItemArgs): Promise<SyncResponseWithCommand> {
    const response = await request<SyncResponseWithCommand>(
      "POST",
      this.syncApiBase,
      ENDPOINT_SYNC_DELETE_ITEMS,
      // this.authToken,
      args,
    );

    return response.data;
  }

  async reorderItems(args: ReorderItemsArgs): Promise<SyncResponseWithCommand> {
    const response = await request<SyncResponseWithCommand>(
      "POST",
      this.syncApiBase,
      ENDPOINT_SYNC_REORDER_ITEMS,
      // this.authToken,
      args,
    );

    return response.data;
  }

  async updateSection(args: UpdateSectionArgs): Promise<SyncResponse> {
    const response = await request<SyncResponse>(
      "POST",
      this.syncApiBase,
      ENDPOINT_SYNC_UPDATE_SECTIONS,
      // this.authToken,
      args,
    );

    return response.data;
  }

  async reorderSections(
    args: ReorderSectionsArgs,
  ): Promise<SyncResponseWithCommand> {
    const response = await request<SyncResponseWithCommand>(
      "POST",
      this.syncApiBase,
      ENDPOINT_SYNC_REORDER_SECTIONS,
      // this.authToken,
      args,
    );

    return response.data;
  }

  async sync(args: SyncRequest): Promise<SyncResponse> {
    const response = await request<SyncResponse>(
      "POST",
      this.syncApiBase,
      ENDPOINT_SYNC,
      // this.authToken,
      args,
    );

    return response.data;
  }
}
