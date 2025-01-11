import globals from "globals";
import pluginJs from "@eslint/js";
import solid from "eslint-plugin-solid/configs/typescript";
import tseslint from "typescript-eslint";

/** @type {import('eslint').Linter.Config[]} */

export default [
  pluginJs.configs.recommended,
  ...tseslint.config([
    tseslint.configs.recommended,
    {
      rules: {
        "@typescript-eslint/consistent-type-exports": "warn",
        "@typescript-eslint/consistent-type-imports": "warn",

        "@typescript-eslint/no-unused-vars": "warn",
      },
    },
  ]),
  solid,
  {
    files: ["src/**/*.{ts,tsx}"],
    languageOptions: {
      globals: globals.browser,
      parser: tseslint.parser,
      parserOptions: {
        project: "tsconfig.json",
      },
    },
  },
];
