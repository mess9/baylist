import globals from "globals";
import pluginJs from "@eslint/js";
import solid from "eslint-plugin-solid/configs/typescript";
import * as tsParser from "@typescript-eslint/parser";

/** @type {import('eslint').Linter.Config[]} */

export default [
  pluginJs.configs.recommended,
  {
    files: ["src/**/*.{ts,tsx}"],
    ...solid,
    languageOptions: {
      globals: globals.browser,
      parser: tsParser,
      parserOptions: {
        project: "tsconfig.json",
      },
    },
  },
];
