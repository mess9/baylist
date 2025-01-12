import IconTrashSolid from "./hero/trash-solid.svg";
import IconTrashMini from "./hero/trash-mini.svg";
import IconPlusSolid from "./hero/plus-solid.svg";
import IconEllipsisHorizontalSolid from "./hero/ellipsis-horizontal-solid.svg";
import IconCheckCircleOutline from "./hero/check-circle.svg";
import IconCheckCircleEmptyOutline from "./hero/check-circle-empty.svg";
import IconChevronDownMini from "./hero/chevron-down-mini.svg";
import IconChevronRightMini from "./hero/chevron-right-mini.svg";

export {
	IconTrashSolid,
	IconTrashMini,
	IconPlusSolid,
	IconEllipsisHorizontalSolid,
	IconCheckCircleOutline,
	IconCheckCircleEmptyOutline,
	IconChevronDownMini,
	IconChevronRightMini
};

// BareIcon.tsx !!EXAMPLE!!
// import { Box } from "@hope-ui/solid";
// import { splitProps, createResource, createSignal } from "solid-js";

// function fragment(html: string) {
//   const tpl = document.createElement("template");
//   tpl.innerHTML = html;
//   return tpl.content;
// }

// async function importIcon(filename: string) {
//   const module = (await import(`../icons/${filename}.svg`)) as {
//     default: string;
//   };
//   return module.default;
// }

// export function BareIcon(props: {filename: string}) {
//   const [local, rest] = splitProps(props, ["filename"]);
//   const [filename, setFilename] = createSignal("");
//   const [svg] = createResource(filename, importIcon);
//   console.log("render icon")
//   // eslint-disable-next-line solid/reactivity
//   setFilename(local.filename);
//   return (
//     <Box style={{ display: "inline-block" }} {...rest}>
//       {svg.loading ? "" : fragment(svg() as string)}
//     </Box>
//   );
// }
// EASY BUT HZ
// import { createRenderEffect as createEffect, createMemo, type Component } from "solid-js";

// const TrashIcon: Component = () => {
//   createEffect(() => console.log("render icon"));
//   return (
//     <svg
//       xmlns="http://www.w3.org/2000/svg"
//       viewBox="0 0 24 24"
//       fill="currentColor"
//       class="size-6"
//     >
//       <path
//         fill-rule="evenodd"
//         d="M16.5 4.478v.227a48.816 48.816 0 0 1 3.878.512.75.75 0 1 1-.256 1.478l-.209-.035-1.005 13.07a3 3 0 0 1-2.991 2.77H8.084a3 3 0 0 1-2.991-2.77L4.087 6.66l-.209.035a.75.75 0 0 1-.256-1.478A48.567 48.567 0 0 1 7.5 4.705v-.227c0-1.564 1.213-2.9 2.816-2.951a52.662 52.662 0 0 1 3.369 0c1.603.051 2.815 1.387 2.815 2.951Zm-6.136-1.452a51.196 51.196 0 0 1 3.273 0C14.39 3.05 15 3.684 15 4.478v.113a49.488 49.488 0 0 0-6 0v-.113c0-.794.609-1.428 1.364-1.452Zm-.355 5.945a.75.75 0 1 0-1.5.058l.347 9a.75.75 0 1 0 1.499-.058l-.346-9Zm5.48.058a.75.75 0 1 0-1.498-.058l-.347 9a.75.75 0 0 0 1.5.058l.345-9Z"
//         clip-rule="evenodd"
//       />
//     </svg>
//   );
// };
// const TrashIconMemo = createMemo(() => TrashIcon)
// export TrashIconMemo()

// BAD WITH CACHE
// import type { JSXElement} from "solid-js";
// import { /*createSignal,*/ createResource, type Component, splitProps } from "solid-js";

// type IconPropsType = {
//   family: string;
//   name: string;
// };

// const iconCache = new Map<string, JSXElement>();

// // Реестры для семейств иконок
// const iconFamilies: Record<string, (name: string) => Promise<JSXElement | null>> = {
//   hero: async (name) => {
//     const icons: Record<string, () => Promise<JSXElement>> = {
//       trash: async () => (
//         <svg
//           xmlns="http://www.w3.org/2000/svg"
//           viewBox="0 0 24 24"
//           fill="currentColor"
//           class="size-6"
//         >
//           <path
//             fill-rule="evenodd"
//             d="M16.5 4.478v.227a48.816 48.816 0 0 1 3.878.512.75.75 0 1 1-.256 1.478l-.209-.035-1.005 13.07a3 3 0 0 1-2.991 2.77H8.084a3 3 0 0 1-2.991-2.77L4.087 6.66l-.209.035a.75.75 0 0 1-.256-1.478A48.567 48.567 0 0 1 7.5 4.705v-.227c0-1.564 1.213-2.9 2.816-2.951a52.662 52.662 0 0 1 3.369 0c1.603.051 2.815 1.387 2.815 2.951Zm-6.136-1.452a51.196 51.196 0 0 1 3.273 0C14.39 3.05 15 3.684 15 4.478v.113a49.488 49.488 0 0 0-6 0v-.113c0-.794.609-1.428 1.364-1.452Zm-.355 5.945a.75.75 0 1 0-1.5.058l.347 9a.75.75 0 1 0 1.499-.058l-.346-9Zm5.48.058a.75.75 0 1 0-1.498-.058l-.347 9a.75.75 0 0 0 1.5.058l.345-9Z"
//             clip-rule="evenodd"
//           />
//         </svg>
//       ),
//     };
//     return icons[name] ? icons[name]() : null;
//   },

//   // Пример добавления нового семейства (например, "feather")
//   // feather: async (name) => {
//   //   const icons = {
//   //     star: async () => (
//   //       <svg
//   //         xmlns="http://www.w3.org/2000/svg"
//   //         viewBox="0 0 24 24"
//   //         fill="none"
//   //         stroke="currentColor"
//   //         class="size-6"
//   //         stroke-width="2"
//   //         stroke-linecap="round"
//   //         stroke-linejoin="round"
//   //       >
//   //         <polygon points="5 19 10 13 3 9 11 9 14 3 17 9 25 9 18 13 23 19 14 15 5 19" />
//   //       </svg>
//   //     ),
//   //   };
//   //   return icons[name] ? icons[name]() : null;
//   // },
// };

// // Функция для загрузки иконок
// const loadIcon = async (family: string, name: string) => {
//   console.log(`Loading icon: ${family}:${name}`);
//   // const loader = iconFamilies[family]; // Выбираем обработчик для семейства
//   // return loader ? loader(name) : null; // Вызываем обработчик или возвращаем null

//   const cacheKey = `${family}:${name}`; // Генерируем уникальный ключ для кеша

//   // Если иконка уже загружена, возвращаем её
//   if (iconCache.has(cacheKey)) {
//     return iconCache.get(cacheKey) || null;
//   }

//   // Загружаем иконку
//   const loader = iconFamilies[family];
//   const icon = loader ? await loader(name) : null;

//   // Сохраняем в кеш и возвращаем
//   if (icon) {
//     iconCache.set(cacheKey, icon);
//   }

//   return icon;

// };

// // Компонент для отображения иконок
// export const Icon: Component<IconPropsType> = (props) => {
//   // const [iconName, setIconName] = createSignal(props.name);
//   // const [iconFamily, setIconFamily] = createSignal(props.family);
//   const [icPrp] = splitProps(props, ["name", "family"])
//   // Загружаем иконку только по мере необходимости
//   const [icon] = createResource(
//     () => ({ family: icPrp.family, name: icPrp.name }),
//     ({ family, name }) => loadIcon(family, name)
//   );

//   return (
//     <div>
//       {icon.loading && <span>Загрузка...</span>}
//       {icon() || <span>Иконка не найдена</span>}
//     </div>
//   );
// };
