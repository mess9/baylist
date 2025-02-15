import type { Accessor } from "solid-js";
import { onCleanup } from "solid-js";

export function clickAway(el: HTMLElement, accessor: Accessor<() => void>) {
  const handler = (e: MouseEvent) => {
    if (!el.contains(e.target as Node)) {
      accessor()();
    }
  };

  document.addEventListener("mousedown", handler);

  onCleanup(() => {
    document.removeEventListener("mousedown", handler);
  });
}
