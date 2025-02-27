import { TodoistApi } from "/shared/api";
import type { ICategory } from "/widgets/Category/ui/Category";
import type { Item as ItemType, Project } from "/shared/api/types/syncEntities";
import type {
  MoveItemArgs,
  AddItemArgs,
  DeleteItemArgs,
  ReorderItemsArgs,
  ReorderSectionsArgs,
  SyncResponseWithCommand,
  ResourceType,
  SyncResponse,
  UpdateItemArgs,
  UpdateSectionArgs,
} from "/shared/api/types/sync";

export type NoCategoryType = { id: string; items: ItemType[] };

export type CategoriesWithNoCategoriesType = (ICategory | NoCategoryType)[];

const api = new TodoistApi();

export async function fetchProjects(): Promise<Project[]> {
  const response = await api.sync({
    sync_token: "",
    resource_types: ["projects"],
  });

  if (!response.projects) {
    throw new Error("Failed to fetch projects");
  }
  return response.projects;
}

export async function fetchCategoriesWithItems(
  projectId: string,
): Promise<CategoriesWithNoCategoriesType> {
  const project = await api.getProjectData({ project_id: projectId });

  if (!project) {
    throw new Error("Failed to fetch categories");
  }

  //const categoriesWithItems = project.sections.map((section) => ({
  //  ...section,
  //  items: project.items
  //    .filter((item) => item.section_id === section.id)
  //    .sort((a, b) => a.child_order - b.child_order),
  //}));

  // const categoriesWithItems2 = project.items.reduce(
  //   (
  //     acc: {
  //       [key: string]: ICategory | { id: "no_category"; items: ItemType[] };
  //     },
  //     item,
  //   ) => {
  //     // eslint-disable-next-line @typescript-eslint/no-unused-expressions
  //     (item.section_id !== null &&
  //       ((acc[item.section_id] &&
  //         (acc[item.section_id] = {
  //           ...acc[item.section_id],
  //           items: [...(acc[item.section_id]?.items || []), item],
  //         })) ||
  //         (acc[item.section_id] = {
  //           ...(project.sections.find((s) => s.id === item.section_id) || {
  //             id: "no_category",
  //           }),
  //           items: [...(acc[item.section_id]?.items || []), item],
  //         }))) ||
  //       (acc["no_category"] = {
  //         id: "no_category",
  //         items: [...(acc["no_category"]?.items || []), item],
  //       });

  //     return acc;
  //   },
  //   {},
  // );
  const sections: CategoriesWithNoCategoriesType = project.sections.map(e => ({...e, items: []}));
  sections.push({ id: "no_category", items: [] });

  const categoriesWithItems2 = project.items.reduce((
		acc: CategoriesWithNoCategoriesType
		, item: ItemType
    ) => {
    const sectionId = item.section_id || "no_category";

    const category = acc.find((cat) => cat.id === sectionId);

    category?.items.push(item);

    return acc;
  }, sections);

  //console.log(categoriesWithItems2)

  return categoriesWithItems2;
}

export async function updateCategoryCollapsed(
  categoryId: string,
  collapsed: boolean,
): Promise<SyncResponse> {
  const commands: UpdateSectionArgs = {
    commands: [
      {
        type: "section_update",
        uuid: crypto.randomUUID(),
        args: { id: categoryId, collapsed },
      },
    ],
  };

  const response = await api.updateSection(commands);

  return response;
}

export async function updateCategoriesOrder(
  categories: ICategory[],
): Promise<void> {
  const sections = categories.map((category, index) => ({
    id: category.id,
    section_order: index + 1,
  }));

  const commands: ReorderSectionsArgs = {
    commands: [
      {
        type: "section_reorder",
        uuid: crypto.randomUUID(),
        args: {
          sections,
        },
      },
    ],
  };

  await api.reorderSections(commands);
}

export async function updateItemsOrder(
  categoryId: string,
  items: ItemType[],
): Promise<void> {
  const itemOrder = items.map((item, index) => ({
    id: item.id,
    child_order: index + 1,
  }));
  const commands: ReorderItemsArgs = {
    commands: [
      {
        type: "item_reorder",
        uuid: crypto.randomUUID(),
        args: {
          items: itemOrder,
        },
      },
    ],
  };

  await api.reorderItems(commands);
}

export async function addItem(
  params: AddItemArgs["commands"][number]["args"],
): Promise<SyncResponseWithCommand> {
  const itemData =
    "item" in params
      ? params.item
      : {
          content: params.content,
          section_id: params.section_id,
          project_id: params.project_id,
        };

  const commands: AddItemArgs = {
    commands: [
      {
        type: "item_add",
        temp_id: crypto.randomUUID(),
        uuid: crypto.randomUUID(),
        args: {
          ...itemData,
        },
      },
    ],
  };

  const response = await api.addItem(commands);

  return response;
}

export async function updateItem(
  params: UpdateItemArgs["commands"][number]["args"],
): Promise<void> {
  const commands: UpdateItemArgs = {
    commands: [
      {
        type: "item_update",
        uuid: crypto.randomUUID(),
        args: {
          ...params,
        },
      },
    ],
  };

  await api.updateItem(commands);
}

export async function moveItem(
  itemId: string,
  newParentId: string | null,
  newItemsFromList: ItemType[],
  newItemsToList: ItemType[],
  projectId?: string,
): Promise<void> {
  const commands: MoveItemArgs = {
    commands: [
      {
        type: "item_move",
        uuid: crypto.randomUUID(),
        args: {
          id: itemId,
          section_id: newParentId || undefined,
		  project_id: projectId || undefined,
        },
      },
      {
        type: "item_reorder",
        uuid: crypto.randomUUID(),
        args: {
          items: newItemsFromList.map((item) => ({
            id: item.id,
            child_order: item.child_order,
          })),
        },
      },
      {
        type: "item_reorder",
        uuid: crypto.randomUUID(),
        args: {
          items: newItemsToList.map((item) => ({
            id: item.id,
            child_order: item.child_order,
          })),
        },
      },
    ],
  };
  await api.moveItem(commands);
}

export async function deleteItem(itemId: string): Promise<void> {
  const commands: DeleteItemArgs = {
    commands: [
      {
        type: "item_delete",
        uuid: crypto.randomUUID(),
        args: {
          id: itemId,
        },
      },
    ],
  };
  await api.deleteItem(commands);
}

export async function sync(
  sync_token: string,
  entities: ResourceType[],
): Promise<SyncResponse> {
  const entitiesResponse = await api.sync({
    sync_token,
    resource_types: entities,
  });
  return entitiesResponse;
}
