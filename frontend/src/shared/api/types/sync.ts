import type {
  Item,
  ProjectNote,
  Project,
  Section,
  ItemNote,
  CompletedInfo,
  Filter,
  Reminder,
  Label,
  User,
  CollaboratorState,
  LiveNotification,
  Duration,
} from "./syncEntities";

export interface SyncCommand<T> {
  type: string;
  uuid: string;
  args: T;
  temp_id?: string;
}

export type ResourceType =
  | "labels"
  | "projects"
  | "items"
  | "notes"
  | "sections"
  | "filters"
  | "reminders"
  | "reminders_location"
  | "locations"
  | "user"
  | "live_notifications"
  | "collaborators"
  | "user_settings"
  | "notification_settings"
  | "user_plan_limits"
  | "completed_info"
  | "stats";

export type SyncRequest = {
  sync_token: string;
  resource_types: ResourceType[];
};

export type SyncResponse = {
  sync_token: string;
  full_sync: boolean;
  user?: User;
  projects?: Project[];
  items?: Item[];
  notes?: ItemNote[];
  project_notes?: ProjectNote[];
  sections?: Section[];
  labels?: Label[];
  filters?: Filter[];
  day_orders?: Record<string, unknown>;
  reminders?: Reminder[];
  collaborators?: Record<string, unknown>;
  collaborators_states?: CollaboratorState[];
  completed_info?: CompletedInfo[];
  live_notifications?: LiveNotification[];
  live_notifications_last_read?: string;
  user_settings?: Record<string, unknown>;
  user_plan_limits?: Record<string, unknown>;
};

export interface SyncRequestWithCommand {
  commands?: SyncCommand<unknown>[];
  resource_types?: string[];
}

export interface CommandError {
  error: string;
  error_code: number;
}

export interface SyncResponseWithCommand {
  sync_status: Record<string, "ok" | CommandError>;
  sync_token: string;
  temp_id_mapping?: Record<string, string>;
  [key: string]: unknown;
}

export type GetProjectDataArgs = {
  project_id: string;
};

export type GetProjectDataResponse = {
  project: Project;
  items: Array<Item>;
  sections: Array<Section>;
  project_notes: Array<ProjectNote>;
};

export type UpdateSectionArgs = {
  commands: SyncCommand<{
    id: string;
    name?: string;
    collapsed?: boolean;
  }>[];
};

export type ReorderSectionsArgs = {
  commands: SyncCommand<{
    sections: {
      id: string;
      section_order: number;
    }[];
  }>[];
};

export interface ReorderItem {
  id: string;
  child_order: number;
}

export type AddItemArgs = {
  commands: SyncCommand<
    | { content: string; section_id: string; project_id: string }
    | {
        item: Item;
      }
  >[];
};

export type UpdateItemArgs = {
  commands: SyncCommand<{
    id: string;
    content?: string;
    description?: string;
    project_id?: string;
    due?: unknown; // Можно уточнить тип, если известен
    deadline?: unknown; // Можно уточнить тип, если известен
    priority?: number;
    parent_id?: string | null;
    child_order?: number;
    section_id?: string;
    day_order?: number | null;
    assigned_by_uid?: string | null;
    responsible_uid?: string | null;
    is_deleted?: boolean;
    sync_id?: string | null;
    completed_at?: string | null;
    duration?: Duration | null;
  }>[];
};

export type MoveItemArgs = {
  commands: (
    | SyncCommand<{ id: string; section_id: string }>
    | SyncCommand<{ items: { id: string; child_order: number }[] }>
  )[];
};

export type ReorderItemsArgs = {
  commands: SyncCommand<{
    items: {
      id: string;
      child_order: number;
    }[];
  }>[];
};

export type DeleteItemArgs = {
  commands: SyncCommand<{
    id: string;
  }>[];
};
