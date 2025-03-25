import { ErrorBoundary } from "solid-js";
import { Router, Route } from "@solidjs/router";
import { onMount } from "solid-js";
import BuyList from "/pages/BuyList";
import BaseTest from "/shared/ui/TestComponents";

import "./index.css";

const backUrl = import.meta.env.VITE_BACK_URL;

export default function App() {
  // @ts-expect-error beta try
  const tg = window.Telegram?.WebApp;

  onMount(() => {
    tg?.expand();
  });

  console.dir(tg.initDataUnsafe);
  const initDataUnsafe = tg.initDataUnsafe;

  const reqQP = new URLSearchParams({
    id: initDataUnsafe?.user?.id,
    first_name: initDataUnsafe?.user?.first_name,
    username: initDataUnsafe?.user?.username,
    auth_date: initDataUnsafe?.auth_date,
    hash: initDataUnsafe?.hash,
  }).toString();

  fetch(`${backUrl}/api/auth/telegram?${reqQP}`, {
    method: "POST",
    mode: "no-cors",
  })
    .then((res) => res.json())
    .then((res) => console.log(res.token))
    .catch((err) => console.log(err, "Im Err"));

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
