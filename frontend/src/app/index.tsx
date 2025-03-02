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

  console.dir(tg.initDataUnsafe)
  const initDataUnsafe = tg.initDataUnsafe;

  const reqQP = { id: initDataUnsafe?.user?.id, first_name: initDataUnsafe?.user?.first_name, username: initDataUnsafe?.user?.username, auth_data: initDataUnsafe?.auth_date, hash: initDataUnsafe?.hash }

  fetch(`http://152.70.174.74:8080/api/auth/telegram?id=${reqQP?.id}&first_name=${reqQP?.first_name}&username=${reqQP?.username}&auth_data=${reqQP?.auth_data}&hash=${reqQP?.hash}`, {method: "POST"})
	.then(res=>res.json()).then(res=>console.log(res.token))
	.catch(err=>console.log(err, "Im Err"));
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
