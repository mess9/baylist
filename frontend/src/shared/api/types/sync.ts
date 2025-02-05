export type SectionSync = {
  added_at: string;
  archived_at: null | string;
  id: string;
  is_archived: boolean;
  is_collapsed: boolean;
  is_deleted: boolean;
  name: string;
  project_id: string;
  section_order: number;
  sync_id: null | string;
  updated_at: string;
  user_id: string;
};

export interface SyncCommand<T> {
  type: string;
  uuid: string;
  args: T;
  temp_id?: string;
}

export interface SyncRequest {
  commands?: SyncCommand<unknown>[];
  resource_types?: string[];
}

export interface CommandError {
  error: string;
  error_code: number;
}

export interface SyncResponse {
  sync_status: Record<string, "ok" | CommandError>;
  temp_id_mapping?: Record<string, string>;
  [key: string]: unknown;
}

export interface ReorderItem {
  id: string;
  child_order: number;
}

interface ReorderSection {
  id: string;
  section_order: number;
}

export type MoveItemArgs = {
  commands: (
    | SyncCommand<{ id: string; section_id: string }>
    | SyncCommand<{ items: ReorderItem[] }>
  )[];
};

export type ReorderItemsArgs = {
  commands: SyncCommand<{ items: ReorderItem[] }>[];
};

export type ReorderSectionsArgs = {
  commands: SyncCommand<{ sections: ReorderSection[] }>[];
};

export type AddItemArgs = {
  commands: SyncCommand<{
    content: string;
    description?: string;
    project_id?: string;
    due?: DueDate;
    deadline?: Deadline;
    priority?: number;
    parent_id?: string | null;
    child_order?: number;
    section_id?: string | null;
    day_order?: number;
    collapsed?: boolean;
    labels?: string[];
    assigned_by_uid?: string;
    responsible_uid?: string | null;
    auto_reminder?: boolean;
    auto_parse_labels?: boolean;
    duration?: Duration;
  }>[];
};

export type UpdateItemArgs = {
  commands: SyncCommand<{
    id: string;
    content?: string;
    description?: string;
    due?: DueDate;
    deadline?: Deadline;
    priority?: number;
    collapsed?: boolean;
    labels?: string[];
    assigned_by_uid?: string;
    responsible_uid?: string | null;
    day_order?: number;
    duration?: Duration | null;
  }>[];
};

export type DeleteItemArgs = {
  commands: SyncCommand<{
    id: string;
  }>[];
};

interface DueDate {
  date: string; // Дата в формате YYYY-MM-DD или YYYY-MM-DDTHH:MM:SS или YYYY-MM-DDTHH:MM:SSZ
  timezone: string | null; // Всегда null для плавающих дат, иначе строка для фиксированных
  string: string; // Человекочитаемое представление даты
  lang: string; // Язык для разбора даты
  is_recurring: boolean; // Флаг, указывающий, является ли дата повторяющейся
}

interface Deadline {
  date: string; // Дедлайн в формате YYYY-MM-DD
  lang: string; // Язык для разбора дедлайна
}

interface Duration {
  amount: number;
  unit: "minute" | "day";
}
