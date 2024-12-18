import {defineConfig} from 'vite'
import preact from '@preact/preset-vite'
import path from 'path';

// https://vite.dev/config/
export default defineConfig({
    resolve: {
        alias: {
            src: path.resolve(__dirname, './src'),
            app: path.resolve(__dirname, './app'),
            pages: path.resolve(__dirname, './pages'),
        }
    },
    plugins: [preact()],
})
