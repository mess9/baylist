import type { ParentComponent, Component } from "solid-js";
import { createSignal, For, mergeProps } from "solid-js";

import type { IBuyItem } from "/features/BuyItem/ui/BuyItem";
import BuyItem, { BuyItemOverlay } from "/features/BuyItem/ui/BuyItem";

import {
	IconEllipsisHorizontalSolid,
	IconPlusSolid,
} from "/app/assets/svg/icons";

import {
	createSortable,
	maybeTransformStyle,
	SortableProvider,
	type Id,
} from "@thisbeyond/solid-dnd";

import classes from "./BuyCategory.module.css";

export interface IBuyCategory {
	id: Id;
	name: string;
	type: "buyCategory";
	buyItems: IBuyItem[];
	order: string;
	delimiter?: "top" | "bottom";
}

export const BuyCategoryOverlay: Component<{name: string}> = (props) => {

	return (
		<section class={classes["buy-category-section"]}>
			<div class={classes["buy-category__header"]}>
				<label class={classes["buy-category__drop-down-control-label"]}>
					<h2>{props.name}</h2>
				</label>
				<div class={classes["buy-category__controls"]}>
					<button
						class={classes["buy-category__add-item-button"]}
						type="button"
					>
						<IconPlusSolid />
					</button>
					<button
						class={classes["buy-category__more-button"]}
						type="button"
					>
						<IconEllipsisHorizontalSolid />
					</button>
				</div>
			</div>
		</section>
	);
};

const BuyCategory: ParentComponent<IBuyCategory> = (props) => {
	const [isExpand, setIsExpand] = createSignal(true);
	let ulRef: HTMLUListElement | undefined;

	const merge = mergeProps({ delimiter: "bottom" }, props);

	const sortable = createSortable(props.id, { type: "buyCategory" });
	const sortedBuyItemIds = () => props.buyItems.map((buyItem) => buyItem.id);

	//TODO: МЕМОИЗИРОВАТЬ В ЗАВИСИМОСТИ ОТ КОНТЕНТА?
	const toggleVisibility = () => {
		if (!ulRef) return;

		const ul = ulRef;
		const currentHeight = ul.scrollHeight;
		ul.style.maxHeight = isExpand() ? `${currentHeight}px` : "0";

		requestAnimationFrame(() => {
			ul.style.transition = "max-height 0.3s ease";
			ul.style.maxHeight = isExpand() ? `${currentHeight}px` : "0";
		});

		setIsExpand(!isExpand());
	};

	// Убираем transition после завершения аниPмации (для корректной работы с динамическим контентом)
	// TODO: Не уверен что нужно, когда будет динамика - !!!проверить!!
	const removeTransition = () => {
		if (ulRef) {
			ulRef.style.transition = "";
		}
	};

	return (
		<section
			class={classes["buy-category-section"]}
			ref={sortable.ref}
			style={maybeTransformStyle(sortable.transform)}
		>
			<div
				class={classes["buy-category__header"]}
				{...sortable.dragActivators}
			>
				<label class={classes["buy-category__drop-down-control-label"]}>
					<input
						class={classes["buy-category__drop-down-checbox"]}
						type="checkbox"
						onChange={toggleVisibility}
					/>
					<h2>{props.name}</h2>
				</label>
				<div class={classes["buy-category__controls"]}>
					<button
						class={classes["buy-category__add-item-button"]}
						type="button"
					>
						<IconPlusSolid />
					</button>
					<button
						class={classes["buy-category__more-button"]}
						type="button"
					>
						<IconEllipsisHorizontalSolid />
					</button>
				</div>
			</div>
			<ul
				classList={{
					[classes["buy-category-ul"]]: true,
				}}
				onTransitionEnd={removeTransition}
				ref={ulRef}
			>
				<SortableProvider ids={sortedBuyItemIds()}>
					<For each={props.buyItems}>
						{(buyItem) => (
							<li
								classList={{
									[classes["buy-list__item-li"]]: true,
									[classes[
										"buy-category__item-li--delimeter-top"
									]]: merge.delimiter === "top",
									[classes[
										"buy-category__item-li--delimeter-bottom"
									]]: merge.delimiter === "bottom",
								}}
							>
								<BuyItem {...buyItem} />
							</li>
						)}
					</For>
				</SortableProvider>
			</ul>
		</section>
	);
};

export default BuyCategory;
