import { defineConfig } from "vite";
import solidPlugin from "vite-plugin-solid";
import eslint from 'vite-plugin-eslint';
import path from "path";

export default defineConfig({
  resolve: {
    alias: {
      "/app": path.resolve(__dirname, "src/app"),
      "/pages": path.resolve(__dirname, "src/pages"),
      "/widgets": path.resolve(__dirname, "src/widgets"),
      "/features": path.resolve(__dirname, "src/features"),
      "/shared": path.resolve(__dirname, "src/shared"),
      "/assets": path.resolve(__dirname, "src/app/assets"),
    },
  },
  plugins: [solidPlugin(), eslint()],
  server: {
    port: 3000,
  },
  build: {
    target: "esnext",
  },
});
