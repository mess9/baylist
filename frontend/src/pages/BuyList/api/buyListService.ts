import { TodoistApi } from "/shared/api";
import type { ICategory } from "/widgets/Category/ui/Category";
import type { IItem } from "/features/Item/ui/Item";
import type {
  MoveItemArgs,
  AddItemArgs,
  UpdateItemArgs,
  DeleteItemArgs,
  ReorderItemsArgs,
  ReorderSectionsArgs,
} from "/shared/api/types/sync";
import { token } from "/src/ign";

const api = new TodoistApi(token);

export async function fetchCategories(): Promise<ICategory[]> {
  const projects = await api.getProjects();
  const project = projects.filter((project) => project.name === "Test");

  if (project.length !== 1) {
    throw new Error('"buylist" not exist or more than one');
  }

  const categories = await api.getSections(project[0].id);

  if (!categories) {
    throw new Error("Failed to fetch categories");
  }

  const categoriesWithItems = await Promise.all(
    categories.map(async (category) => {
      const items = await api.getTasks({ sectionId: category.id });
      return {
        ...category,
        items: items.sort((a, b) => a.order - b.order) || [],
      };
    })
  );

  return categoriesWithItems.sort((a, b) => a.order - b.order);
}

export async function updateCategoriesOrder(
  categories: ICategory[]
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
  console.log("Все категории обновлены на сервере");
}

export async function updateItemsOrder(
  categoryId: string,
  items: IItem[]
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
  console.log(`Items in category ${categoryId} updated on the server`);
}

export async function addItem(item: IItem, section_id: string): Promise<void> {
  const commands: AddItemArgs = {
    commands: [
      {
        type: "item_add",
        temp_id: crypto.randomUUID(),
        uuid: crypto.randomUUID(),
        args: {
          content: item.content,
          project_id: section_id,
          labels: item.labels,
        },
      },
    ],
  };

  const response = await api.addItem(commands);
  const tempId = commands.commands[0]?.temp_id;
  const addedItemId = response.temp_id_mapping?.[tempId ?? ""];

  if (addedItemId) {
    console.log(`Элемент добавлен с ID: ${addedItemId}`);
  } else {
    console.log("Ошибка: не удалось добавить элемент");
  }
}

export async function updateItem(item: IItem): Promise<void> {
  const commands: UpdateItemArgs = {
    commands: [
      {
        type: "item_update",
        uuid: crypto.randomUUID(),
        args: {
          id: item.id,
          content: item.content,
          description: item.description,
          // due: item.due,
          // deadline: item.deadline, TODO: add due and deadline
          priority: item.priority,
          labels: item.labels,
        },
      },
    ],
  };

  await api.updateItem(commands);
  console.log(`Item ${item.id} updated on the server`);
}

export async function moveItem(
  itemId: string,
  newParentId: string,
  newItemsFromList: IItem[],
  newItemsToList: IItem[]
): Promise<void> {
  const commands: MoveItemArgs = {
    commands: [
      {
        type: "item_move",
        uuid: crypto.randomUUID(),
        args: {
          id: itemId,
          section_id: newParentId,
        },
      },
      {
        type: "item_reorder",
        uuid: crypto.randomUUID(),
        args: {
          items: newItemsFromList.map((item) => ({
            id: item.id,
            child_order: item.order,
          })),
        },
      },
      {
        type: "item_reorder",
        uuid: crypto.randomUUID(),
        args: {
          items: newItemsToList.map((item) => ({
            id: item.id,
            child_order: item.order,
          })),
        },
      },
    ],
  };

  await api.moveItem(commands);
  console.log(`Item ${itemId} moved to category ${newParentId}`);
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
  console.log(`Item ${itemId} deleted from the server`);
}
