import type { Static } from "runtypes";
import {
  Literal,
  Record as RecordRunType,
  Number as NumberRunType,
  Union,
  String as StringRunType,
  Boolean as BooleanRunType,
  Array as ArrayRunType,
  Null,
} from "runtypes";

export const Project = RecordRunType({
  id: StringRunType,
  name: StringRunType,
  color: StringRunType,
  parent_id: StringRunType.Or(Null),
  child_order: NumberRunType,
  collapsed: BooleanRunType,
  view_style: StringRunType,
  is_deleted: BooleanRunType,
  is_archived: BooleanRunType,
  sync_id: StringRunType.Or(Null),
  archived_timestamp: NumberRunType.Or(Null),
  shared: BooleanRunType,
  can_assign_tasks: BooleanRunType,
  is_favorite: BooleanRunType,
  inbox_project: BooleanRunType,
  team_inbox: BooleanRunType,
  user_id: StringRunType,
});

export type Project = Static<typeof Project>;

export const ProjectNote = RecordRunType({
  id: StringRunType,
  posted_uid: StringRunType,
  project_id: StringRunType,
  content: StringRunType,
  file_attachment: StringRunType.Or(Null),
  uids_to_notify: ArrayRunType(StringRunType).Or(Null),
  is_deleted: BooleanRunType,
  posted_at: StringRunType,
  reactions: StringRunType.Or(Null),
});

export type ProjectNote = Static<typeof ProjectNote>;

export const Section = RecordRunType({
  id: StringRunType,
  name: StringRunType,
  project_id: StringRunType,
  section_order: NumberRunType,
  collapsed: BooleanRunType,
  user_id: StringRunType,
  sync_id: StringRunType.Or(Null),
  is_deleted: BooleanRunType,
  is_archived: BooleanRunType,
  archived_at: StringRunType.Or(Null),
  added_at: StringRunType,
  updated_at: StringRunType,
});

export type Section = Static<typeof Section>;

export const Item = RecordRunType({
  id: StringRunType,
  user_id: StringRunType,
  project_id: StringRunType,
  content: StringRunType,
  description: StringRunType,
  priority: NumberRunType,
  collapsed: BooleanRunType,
  labels: ArrayRunType(StringRunType),
  checked: BooleanRunType,
  added_at: StringRunType,
  due: Null.Or(
    RecordRunType({
      date: StringRunType,
      timezone: StringRunType.Or(Null),
      string: StringRunType,
      lang: StringRunType,
      is_recurring: BooleanRunType,
    })
  ),
  deadline: Null.Or(
    RecordRunType({
      date: StringRunType,
      lang: StringRunType,
    })
  ),
  parent_id: StringRunType.Or(Null),
  child_order: NumberRunType,
  section_id: StringRunType,
  day_order: NumberRunType.Or(Null),
  assigned_by_uid: StringRunType.Or(Null),
  responsible_uid: StringRunType.Or(Null),
  is_deleted: BooleanRunType,
  sync_id: StringRunType.Or(Null),
  completed_at: StringRunType.Or(Null),
  duration: Null.Or(
    RecordRunType({
      amount: NumberRunType.withConstraint(
        (n) =>
          (Number.isInteger(n) && n > 0) || "Value should be a positive integer"
      ),
      unit: Union(Literal("minute"), Literal("day")),
    })
  ),
});

export type Item = Static<typeof Item>;

export const ItemNote = RecordRunType({
  id: StringRunType,
  posted_uid: StringRunType,
  item_id: StringRunType,
  content: StringRunType,
  file_attachment: StringRunType.Or(Null),
  uids_to_notify: ArrayRunType(StringRunType).Or(Null),
  is_deleted: BooleanRunType,
  posted_at: StringRunType,
  reactions: StringRunType.Or(Null),
});

export type ItemNote = Static<typeof ItemNote>;

export const FileAttachments = RecordRunType({
  file_name: StringRunType,
  file_size: NumberRunType,
  file_type: StringRunType,
  file_url: StringRunType,
  upload_state: StringRunType,
});

export type FileAttachments = Static<typeof FileAttachments>;

export const Duration = RecordRunType({
  amount: NumberRunType.withConstraint(
    (n) =>
      (Number.isInteger(n) && n > 0) || "Value should be a positive integer"
  ),
  unit: Union(Literal("minute"), Literal("day")),
});

export type Duration = Static<typeof Duration>;

export const Label = RecordRunType({
  id: StringRunType,
  name: StringRunType,
  color: StringRunType,
  item_order: NumberRunType,
  is_deleted: BooleanRunType,
  is_favorite: BooleanRunType,
});

export type Label = Static<typeof Label>;

export const Filter = RecordRunType({
  id: StringRunType,
  name: StringRunType,
  query: StringRunType,
  color: StringRunType,
  item_order: NumberRunType,
  is_deleted: BooleanRunType,
  is_favorite: BooleanRunType,
});

export type Filter = Static<typeof Filter>;

export const Reminder = RecordRunType({
  id: StringRunType,
  notify_uid: StringRunType,
  item_id: StringRunType,
  type: StringRunType,
  due: Null.Or(
    RecordRunType({
      date: StringRunType,
      timezone: StringRunType.Or(Null),
      is_recurring: BooleanRunType,
      string: StringRunType,
      lang: StringRunType,
    })
  ),
  minute_offset: NumberRunType,
  name: StringRunType,
  loc_lat: StringRunType,
  loc_long: StringRunType,
  loc_trigger: StringRunType,
  radius: NumberRunType,
  is_deleted: BooleanRunType,
});

export type Reminder = Static<typeof Reminder>;

export const Collaborator = RecordRunType({
  id: StringRunType,
  email: StringRunType,
  full_name: StringRunType,
  timezone: StringRunType,
  image_id: StringRunType,
});

export type Collaborator = Static<typeof Collaborator>;

export const CompletedInfo = RecordRunType({
  project_id: StringRunType.Or(Null),
  section_id: StringRunType.Or(Null),
  item_id: StringRunType.Or(Null),
  completed_items: NumberRunType,
  archived_sections: NumberRunType.Or(Null),
});

export type CompletedInfo = Static<typeof CompletedInfo>;

export const UserSettings = RecordRunType({
  reminder_push: BooleanRunType,
  reminder_desktop: BooleanRunType,
  reminder_email: BooleanRunType,
  completed_sound_desktop: BooleanRunType,
  completed_sound_mobile: BooleanRunType,
});

export type UserSettings = Static<typeof UserSettings>;

export const UserPlanLimits = RecordRunType({
  current: RecordRunType({}).Or(RecordRunType({})),
  next: RecordRunType({}).Or(RecordRunType({})).Or(Null),
});

export type UserPlanLimits = Static<typeof UserPlanLimits>;

export const User = RecordRunType({
  auto_reminder: NumberRunType,
  avatar_big: StringRunType,
  avatar_medium: StringRunType,
  avatar_s640: StringRunType,
  avatar_small: StringRunType,
  business_account_id: StringRunType,
  daily_goal: NumberRunType,
  date_format: NumberRunType,
  dateist_lang: StringRunType.Or(Null),
  days_off: ArrayRunType(NumberRunType),
  email: StringRunType,
  feature_identifier: StringRunType,
  features: RecordRunType({}).Or(RecordRunType({})),
  full_name: StringRunType,
  has_password: BooleanRunType,
  id: StringRunType,
  image_id: StringRunType,
  inbox_project_id: StringRunType,
  is_biz_admin: BooleanRunType,
  is_premium: BooleanRunType,
  joined_at: StringRunType.Or(Null),
  karma: NumberRunType,
  karma_trend: StringRunType,
  lang: StringRunType,
  next_week: NumberRunType,
  premium_status: StringRunType,
  premium_until: StringRunType.Or(Null),
  sort_order: NumberRunType,
  start_day: NumberRunType,
  start_page: StringRunType,
  team_inbox_id: StringRunType,
  theme_id: StringRunType,
  time_format: NumberRunType,
  token: StringRunType,
  tz_info: RecordRunType({
    timezone: StringRunType,
    hours: NumberRunType,
    minutes: NumberRunType,
    is_dst: BooleanRunType,
    gmt_string: StringRunType,
  }),
  weekend_start_day: NumberRunType,
  verification_status: StringRunType,
});

export type User = Static<typeof User>;

export const CollaboratorState = RecordRunType({
  project_id: StringRunType,
  user_id: StringRunType,
  state: Union(Literal("active"), Literal("invited")),
  is_deleted: BooleanRunType,
});

export type CollaboratorState = Static<typeof CollaboratorState>;

export const LiveNotification = Union(
  Literal("share_invitation_sent"),
  Literal("share_invitation_accepted"),
  Literal("share_invitation_rejected"),
  Literal("user_left_project"),
  Literal("user_removed_from_project"),
  Literal("item_assigned"),
  Literal("item_completed"),
  Literal("item_uncompleted"),
  Literal("note_added"),
  Literal("biz_policy_disallowed_invitation"),
  Literal("biz_policy_rejected_invitation"),
  Literal("biz_trial_will_end"),
  Literal("biz_payment_failed"),
  Literal("biz_account_disabled"),
  Literal("biz_invitation_created"),
  Literal("biz_invitation_accepted"),
  Literal("biz_invitation_rejected"),
  Literal("daily_goal_reached"),
  Literal("weekly_goal_reached")
);

export type LiveNotification = Static<typeof LiveNotification>;
