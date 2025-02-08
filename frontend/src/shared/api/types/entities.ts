import type { Static } from "runtypes";
import {
  Boolean,
  Number as NumberRunType,
  String,
  Array,
  Record as RecordRunType,
  Partial as PartialRunTypes,
  Literal,
  Union,
  Null,
} from "runtypes";

export const Int = NumberRunType.withConstraint(
  (n) =>
    Number.isInteger(n) || `${n} is not a valid entity id. Should be a string`
);

export type TodoistEntity = {
  id: string;
};

export type OrderedEntity = TodoistEntity & {
  order: number;
};

export type EntityInHierarchy = OrderedEntity & {
  parentId?: string | null;
};

export const DueDate = RecordRunType({
  isRecurring: Boolean,
  string: String,
  date: String,
}).And(
  PartialRunTypes({
    datetime: String.Or(Null),
    timezone: String.Or(Null),
    lang: String.Or(Null),
  })
);

export type DueDate = Static<typeof DueDate>;

export const Duration = RecordRunType({
  amount: NumberRunType.withConstraint(
    (n) => n > 0 || "Value should be greater than zero"
  ),
  unit: Union(Literal("minute"), Literal("day")),
});

export type Duration = Static<typeof Duration>;

export const Task = RecordRunType({
  id: String,
  order: Int,
  content: String,
  description: String,
  projectId: String,
  isCompleted: Boolean,
  labels: Array(String),
  priority: Int,
  commentCount: Int,
  createdAt: String,
  url: String,
  creatorId: String,
}).And(
  PartialRunTypes({
    due: DueDate.Or(Null),
    duration: Duration.Or(Null),
    assigneeId: String.Or(Null),
    assignerId: String.Or(Null),
    parentId: String.Or(Null),
    sectionId: String.Or(Null),
  })
);

export type Task = Static<typeof Task>;

export const TaskSync = Task.And(
  RecordRunType({
    comment_count: Int,
    child_order: Int,
    user_id: String,
    is_archived: Boolean,
    is_deleted: Boolean,
    collapsed: Boolean,
    parent_id: String.Or(Null),
    archived_timestamp: Int,
    color: String,
    view_style: String,
  })
);

export type TaskSync = Static<typeof TaskSync>;

export const Project = RecordRunType({
  id: String,
  name: String,
  color: String,
  commentCount: Int,
  isShared: Boolean,
  isFavorite: Boolean,
  url: String,
  isInboxProject: Boolean,
  isTeamInbox: Boolean,
  order: Int,
  viewStyle: String,
}).And(
  PartialRunTypes({
    parentId: String.Or(Null),
  })
);

export type Project = Static<typeof Project>;

export const ProjectSync = Project.And(
  RecordRunType({
    child_order: Int,
    user_id: String,
    is_archived: Boolean,
    is_deleted: Boolean,
    collapsed: Boolean,
    parent_id: String.Or(Null),
    archived_timestamp: Int,
    color: String,
    view_style: String,
  })
);

export type ProjectSync = Static<typeof ProjectSync>;

export const Section = RecordRunType({
  id: String,
  order: Int,
  name: String,
  projectId: String,
});

export type Section = Static<typeof Section>;

export const SectionSync = Section.And(
  RecordRunType({
    sync_id: String.Or(Null),
    is_deleted: Boolean,
    is_archived: Boolean,
    archived_at: String.Or(Null),
    added_at: String,
    updated_at: String,
  })
);

export type SectionSync = Static<typeof SectionSync>;

export const Label = RecordRunType({
  id: String,
  order: Int,
  name: String,
  color: String,
  isFavorite: Boolean,
});

export type Label = Static<typeof Label>;

export const Attachment = RecordRunType({
  resourceType: String,
}).And(
  PartialRunTypes({
    fileName: String.Or(Null),
    fileSize: Int.Or(Null),
    fileType: String.Or(Null),
    fileUrl: String.Or(Null),
    fileDuration: Int.Or(Null),
    uploadState: Union(Literal("pending"), Literal("completed")).Or(Null),
    image: String.Or(Null),
    imageWidth: Int.Or(Null),
    imageHeight: Int.Or(Null),
    url: String.Or(Null),
    title: String.Or(Null),
  })
);

export type Attachment = Static<typeof Attachment>;

export const Comment = RecordRunType({
  id: String,
  content: String,
  postedAt: String,
}).And(
  PartialRunTypes({
    taskId: String.Or(Null),
    projectId: String.Or(Null),
    attachment: Attachment.Or(Null),
  })
);

export type Comment = Static<typeof Comment>;

export const User = RecordRunType({
  id: String,
  name: String,
  email: String,
});

export type User = Static<typeof User>;

export type Color = {
  /**
   * @deprecated No longer used
   */
  id: number;
  /**
   * The key of the color (i.e. 'berry_red')
   */
  key: string;
  /**
   * The display name of the color (i.e. 'Berry Red')
   */
  displayName: string;
  /**
   * @deprecated Use {@link Color.displayName} instead
   */
  name: string;
  /**
   * The hex value of the color (i.e. '#b8255f')
   */
  hexValue: string;
  /**
   * @deprecated Use {@link Color.hexValue} instead
   */
  value: string;
};

export type QuickAddTaskResponse = {
  id: string;
  projectId: string;
  content: string;
  description: string;
  priority: number;
  sectionId: string | null;
  parentId: string | null;
  childOrder: number; // order
  labels: string[];
  responsibleUid: string | null;
  checked: boolean; // completed
  addedAt: string; // created
  addedByUid: string | null;
  duration: Duration | null;
  due: {
    date: string;
    timezone: string | null;
    isRecurring: boolean;
    string: string;
    lang: string;
  } | null;
};

// This allows us to accept any string during validation, but provide intellisense for the two possible values in request args
export type ProjectViewStyle = "list" | "board";
