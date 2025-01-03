import { ErrorBoundary } from "solid-js";
import { Router, Route } from "@solidjs/router";

import cn from "/shared/utils/cn";

import Logo from "/shared/ui/Logo/Logo";
import Home from "/pages/Home";
import BaseTest from "/shared/ui/TestComponents";

import "./index.css";

export default function App() {
    return (
        <ErrorBoundary
            fallback={(err) => (console.log(err), (<span>{`${err}`}</span>))}
        >
            <Router>
                <Route path="/" component={Home} />
                <Route path="/test" component={BaseTest} />
            </Router>
        </ErrorBoundary>
    );
}
