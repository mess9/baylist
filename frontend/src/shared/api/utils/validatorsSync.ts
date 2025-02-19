import {
  Label,
  Project,
  Section,
  Item,
  User,
  ItemNote,
} from "../types/syncEntities";

export function validateItem(input: unknown): Item {
  return Item.check(input);
}
export function validateItemArray(input: unknown[]): Item[] {
  return input.map(validateItem);
}

export function validateProject(input: unknown): Project {
  return Project.check(input);
}
export function validateProjectArray(input: unknown[]): Project[] {
  return input.map(validateProject);
}

export function validateSection(input: unknown): Section {
  return Section.check(input);
}
export function validateSectionArray(input: unknown[]): Section[] {
  return input.map(validateSection);
}

export function validateLabel(input: unknown): Label {
  return Label.check(input);
}
export function validateLabelArray(input: unknown[]): Label[] {
  return input.map(validateLabel);
}

export function validateItemNote(input: unknown): ItemNote {
  return ItemNote.check(input);
}
export function validateItemNoteArray(input: unknown[]): ItemNote[] {
  return input.map(validateItemNote);
}

export function validateUser(input: unknown): User {
  return User.check(input);
}

export function validateUserArray(input: unknown[]): User[] {
  return input.map(validateUser);
}
