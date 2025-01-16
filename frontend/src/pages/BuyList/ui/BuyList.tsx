import type { Component } from "solid-js";
import { batch, For, onMount } from "solid-js";
import { createStore } from "solid-js/store";

import type { IBuyCategory } from "/widgets/BuyCategory/ui/BuyCategory";
import {
    BuyItemOverlay,
    type IBuyItem,
} from "/src/features/BuyItem/ui/BuyItem";
import BuyCategory, {
    BuyCategoryOverlay,
} from "/widgets/BuyCategory/ui/BuyCategory";

import type {
    CollisionDetector,
    DragEventHandler,
    Draggable,
    Droppable,
    Id,
    Transformer,
} from "@thisbeyond/solid-dnd";
import {
    closestCenter,
    DragDropProvider,
    DragDropSensors,
    DragOverlay,
    SortableProvider,
    useDragDropContext,
} from "@thisbeyond/solid-dnd";
import Big from "big.js";

import classes from "./BuyList.module.css";

export const ORDER_DELTA = 1000;

interface Base {
    id: Id;
    name: string;
    type: "buyCategory" | "buyItem";
    order: string;
}

type BuyEntity = Base & (IBuyCategory | IBuyItem);

const sortByOrder = (buyEntities: BuyEntity[]) => {
    const sorted = buyEntities.map((buyItem) => ({
        order: new Big(buyItem.order),
        buyItem,
    }));
    sorted.sort((a, b) => a.order.cmp(b.order));
    return sorted.map((entry) => entry.buyItem);
};

const BuyList: Component = () => {
    const [buyEntities, setBuyEntities] = createStore<Record<Id, BuyEntity>>(
        {},
    );

    let nextOrder = 0;

    const getNextOrder = () => {
        nextOrder += ORDER_DELTA;
        return nextOrder.toString();
    };

    const addGroup = (id: Id, name: string) => {
        setBuyEntities(id, {
            id,
            name,
            type: "buyCategory",
            order: getNextOrder(),
        });
    };

    const addBuyItem = (id: Id, name: string, buyCategory: Id) => {
        setBuyEntities(id, {
            id,
            name,
            buyCategory,
            type: "buyItem",
            order: getNextOrder(),
        });
    };

    const setup = () => {
        batch(() => {
            addGroup(1, "Todo");
            addGroup(2, "In Progress");
            addGroup(3, "Done");
            addBuyItem(4, "Make waves", 1);
            addBuyItem(5, "Party1!.", 1);
            addBuyItem(6, "Party2!.", 1);
            addBuyItem(7, "Party3!.", 1);
            addBuyItem(8, "Party4!.", 1);
            addBuyItem(9, "Meet friends.", 2);
            addBuyItem(10, "Do shopping.", 3);
        });
    };

    onMount(setup);

    const buyCategories = () =>
        sortByOrder(
            Object.values(buyEntities).filter(
                (buyItem) => buyItem.type === "buyCategory",
            ),
        ) as IBuyCategory[];

    const buyCategoryIds = () =>
        buyCategories().map((buyCategory) => buyCategory.id);

    const buyCategoryOrders = () =>
        buyCategories().map((buyCategory) => buyCategory.order);

    const buyCategoryItems = (buyCategoryId: Id) =>
        sortByOrder(
            Object.values(buyEntities).filter(
                (buyEntity) =>
                    buyEntity.type === "buyItem" &&
                    buyEntity.buyCategory === buyCategoryId,
            ),
        ) as IBuyItem[];

    const buyCategoryItemIds = (buyCategoryId: Id) =>
        buyCategoryItems(buyCategoryId).map((buyItem) => buyItem.id);

    const buyCategoryItemOrders = (buyCategoryId: Id) =>
        buyCategoryItems(buyCategoryId).map((buyItem) => buyItem.order);

    const isSortableBuyCategory = (sortable: Draggable | Droppable) =>
        sortable.data.type === "buyCategory";

    const closestBuyEntity: CollisionDetector = (
        draggable,
        droppables,
        context,
    ) => {
        const closestBuyCategory = closestCenter(
            draggable,
            droppables.filter((droppable) => isSortableBuyCategory(droppable)),
            context,
        );
        if (isSortableBuyCategory(draggable)) {
            return closestBuyCategory || null;
        } else if (closestBuyCategory) {
            const closestBuyItem = closestCenter(
                draggable,
                droppables.filter(
                    (droppable) =>
                        !isSortableBuyCategory(droppable) &&
                        droppable.data.buyCategory === closestBuyCategory.id,
                ),
                context,
            );

            if (!closestBuyItem) {
                return closestBuyCategory || null;
            }

            const changingGroup =
                draggable.data.category !== closestBuyCategory.id;
            if (changingGroup) {
                const belowLastBuyItem =
                    buyCategoryItemIds(closestBuyCategory.id).at(-1) ===
                        closestBuyItem.id &&
                    draggable.transformed.center.y >
                        closestBuyItem.transformed.center.y;

                if (belowLastBuyItem) return closestBuyCategory || null;
            }

            return closestBuyItem || null;
        }
        return null;
    };

    const move = (
        draggable: Draggable,
        droppable: Droppable,
        onlyWhenChangingBuyCategory = true,
    ) => {
        if (!draggable || !droppable) return;

        const draggableIsBuyCategory = isSortableBuyCategory(draggable);
        const droppableIsBuyCategory = isSortableBuyCategory(droppable);

        const draggableBuyCategoryId = draggableIsBuyCategory
            ? draggable.id
            : draggable.data.buyCategory;

        const droppableBuyCategoryId = droppableIsBuyCategory
            ? droppable.id
            : droppable.data.buyCategory;

        if (
            onlyWhenChangingBuyCategory &&
            (draggableIsBuyCategory ||
                draggableBuyCategoryId === droppableBuyCategoryId)
        ) {
            return;
        }

        let ids: Id[] = [];
        let orders: string[] = [];
        let order: Big | undefined;

        if (draggableIsBuyCategory) {
            ids = buyCategoryIds();
            orders = buyCategoryOrders();
        } else {
            ids = buyCategoryItemIds(droppableBuyCategoryId);
            orders = buyCategoryItemOrders(droppableBuyCategoryId);
        }

        if (droppableIsBuyCategory && !draggableIsBuyCategory) {
            order = new Big(orders.at(-1) ?? -ORDER_DELTA)
                .plus(ORDER_DELTA)
                .round();
        } else {
            const draggableIndex = ids.indexOf(draggable.id);
            const droppableIndex = ids.indexOf(droppable.id);
            if (draggableIndex !== droppableIndex) {
                let orderAfter, orderBefore;
                if (draggableIndex === -1 || draggableIndex > droppableIndex) {
                    orderBefore = new Big(orders[droppableIndex]);
                    orderAfter = new Big(
                        orders[droppableIndex - 1] ??
                            orderBefore.minus(ORDER_DELTA * 2),
                    );
                } else {
                    orderAfter = new Big(orders[droppableIndex]);
                    orderBefore = new Big(
                        orders[droppableIndex + 1] ??
                            orderAfter.plus(ORDER_DELTA * 2),
                    );
                }

                if (orderAfter !== undefined && orderBefore !== undefined) {
                    order = orderAfter.plus(orderBefore).div(2.0);
                    const rounded = order.round();
                    if (rounded.gt(orderAfter) && rounded.lt(orderBefore)) {
                        order = rounded;
                    }
                }
            }
        }

        if (order !== undefined) {
            setBuyEntities(draggable.id, (buyEntity) => ({
                ...buyEntity,
                order: order.toString(),
                buyCategory: droppableBuyCategoryId,
            }));
        }
    };

    const onDragOver: DragEventHandler = ({ draggable, droppable }) => {
        if (!droppable) return;
        move(draggable, droppable);
    };

    const onDragEnd: DragEventHandler = ({ draggable, droppable }) => {
        if (!droppable) return;
        move(draggable, droppable, false);
    };

    const ConstrainDragAxis = () => {
        const [
            ,
            { onDragStart, onDragEnd, addTransformer, removeTransformer },
        ] = useDragDropContext() as NonNullable<
            ReturnType<typeof useDragDropContext>
        >;

        const transformer: Transformer = {
            id: "constrain-x-axis",
            order: 100,
            callback: (transform) => ({ ...transform, x: 0 }),
        };

        onDragStart(({ draggable }) => {
            addTransformer("draggables", draggable.id, transformer);
        });

        onDragEnd(({ draggable }) => {
            removeTransformer("draggables", draggable.id, transformer.id);
        });

        return <></>;
    };

    return (
        <div class={classes["buy-list-container"]}>
            <DragDropProvider
                onDragOver={onDragOver}
                onDragEnd={onDragEnd}
                collisionDetector={closestBuyEntity}
            >
                <DragDropSensors />
                <ConstrainDragAxis />
                <ul class={classes["buy-list-ul"]}>
                    <SortableProvider ids={buyCategoryIds()}>
                        <For each={buyCategories()}>
                            {(buyCategory) => (
                                <li>
                                    <BuyCategory
                                        {...buyCategory}
                                        buyItems={buyCategoryItems(
                                            buyCategory.id,
                                        )}
                                    />
                                </li>
                            )}
                        </For>
                    </SortableProvider>
                </ul>
                <DragOverlay>
                    {(draggable) => {
                        if (!draggable) return;
                        const buyEntity = buyEntities[draggable.id];
                        console.log(buyEntity);
                        return isSortableBuyCategory(draggable) ? (
                            <BuyCategoryOverlay name={buyEntity.name} />
                        ) : (
                            <BuyItemOverlay name={buyEntity.name} />
                        );
                    }}
                </DragOverlay>
            </DragDropProvider>
        </div>
    );
};

export default BuyList;
