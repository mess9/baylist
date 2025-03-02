import { ErrorBoundary } from "solid-js";
import { Router, Route } from "@solidjs/router";
import { onMount } from "solid-js";
import BuyList from "/pages/BuyList";
import BaseTest from "/shared/ui/TestComponents";

import "./index.css";



export default function App() {
  // @ts-expect-error beta try
  const tg = window.Telegram?.WebApp;

  onMount(() => {
    tg?.expand();
  });
  return (
    <ErrorBoundary
      fallback={(err) => (console.log(err), (<span>{`${err}`}</span>))}
    >
      <Router>
        <Route path="/" component={BuyList} />
        <Route path="/test" component={BaseTest} />
      </Router>
    </ErrorBoundary>
  );
}
