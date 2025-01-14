import type { ParentComponent } from "solid-js";
import { createSignal, For } from "solid-js";

import {
	IconEllipsisHorizontalSolid,
	IconPlusSolid,
} from "/app/assets/svg/icons";

import classes from "./BuyCategory.module.css";

const BuyCategory: ParentComponent<{ delimiter: "top" | "bottom"/*, "category-id": number*/ }> = (
	props,
) => {
	const [isExpand, setIsExpand] = createSignal(true);
	let ulRef: HTMLUListElement | undefined;

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
		<section class={classes["buy-category-section"]}>
			<div class={classes["buy-category__header"]}>
				<label class={classes["buy-category__drop-down-control-label"]}>
					<input
						class={classes["buy-category__drop-down-checbox"]}
						type="checkbox"
						onChange={toggleVisibility}
					/>
					<h2>Section Header</h2>
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
				<For each={[1, 2, 3, 4, 5]}>
					{() => (
						<li
							classList={{
								[classes["buy-list__item-li"]]: true,
								[classes[
									"buy-category__item-li--delimeter-top"
								]]: props.delimiter === "top",
								[classes[
									"buy-category__item-li--delimeter-bottom"
								]]: props.delimiter === "bottom",
							}}
						>
							{props.children}
							{/*{props["category-id"]}*/}
						</li>
					)}
				</For>
			</ul>
		</section>
	);
};

export default BuyCategory;
