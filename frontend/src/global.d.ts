import "solid-js";

declare module "solid-js" {
  namespace JSX {
    interface Directives {
      clickAway: () => void;
    }
  }
}
