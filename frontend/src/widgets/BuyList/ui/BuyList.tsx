import BuyItem from "/features/BuyItem";
import BuyCategory from "/features/BuyCategory/ui/BuyCategory";

import classes from "./BuyList.module.css";

import type { Component } from "solid-js";
import { createSignal, For } from "solid-js";

/*import type { DragEventHandler } from "@thisbeyond/solid-dnd";
import {
	DragDropProvider,
	DragDropSensors,
	SortableProvider,
	createSortable,
	closestCenter,
} from "@thisbeyond/solid-dnd";
import { useDragDropContext } from "@thisbeyond/solid-dnd";

const Sortable: Component<{ item: number }> = (props) => {
	// eslint-disable-next-line solid/reactivity
	const sortable = createSortable(props.item);
	const [state] = useDragDropContext();

	return (
		<div
			use:sortable
			class="sortable"
			classList={{
				"opacity-25": sortable.isActiveDraggable,
				"transition-transform": !!state.active.draggable,
			}}
		>

			<BuyCategory delimiter="bottom" category-id={props.item}>
									
										<BuyItem />
									
								</BuyCategory>
		</div>
	);
};*/

const BuyList: Component = () => {
	const [items /*, setItems*/] = createSignal([1, 2, 3, 4]);
	/*const [activeItem, setActiveItem] = createSignal<number | null>(null);
	const ids = () => items();*/

	/*const onDragStart: DragEventHandler = ({ draggable }) =>
		setActiveItem(draggable.id as number);*/

	/*const onDragEnd: DragEventHandler = ({ draggable, droppable }) => {
		if (draggable && droppable) {
			const currentItems = ids();
			const fromIndex = currentItems.indexOf(draggable.id as number);
			const toIndex = currentItems.indexOf(droppable.id as number);
			if (fromIndex !== toIndex) {
				const updatedItems = currentItems.slice();
				updatedItems.splice(
					toIndex,
					0,
					...updatedItems.splice(fromIndex, 1),
				);
				setItems(updatedItems);
			}
		}
	};*/
	/*<DragDropProvider
			onDragStart={onDragStart}
			onDragEnd={onDragEnd}
			collisionDetector={closestCenter}
		>
			<DragDropSensors />*/
	/*<SortableProvider ids={ids()}>*/
	/*<Sortable item={item} />*/
	/*</SortableProvider>*/
	/*</DragDropProvider>*/
	return (
		<ul class={classes["buy-list-ul"]}>
			<For each={items()}>
				{
					(/*item*/) => (
						<li>
							<BuyCategory delimiter="bottom">
								<BuyItem />
							</BuyCategory>
						</li>
					)
				}
			</For>
		</ul>
	);
};

export default BuyList;
