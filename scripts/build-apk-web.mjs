import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const output = path.join(root, 'android', 'app', 'src', 'main', 'assets', 'www');
await fs.rm(output, { recursive: true, force: true });
await fs.mkdir(output, { recursive: true });

async function copyTree(from, to, include = () => true) {
  await fs.mkdir(to, { recursive: true });
  for (const entry of await fs.readdir(from, { withFileTypes: true })) {
    if (!include(entry.name, entry)) continue;
    const source = path.join(from, entry.name);
    const target = path.join(to, entry.name);
    if (entry.isDirectory()) await copyTree(source, target);
    else if (entry.isFile()) await fs.copyFile(source, target);
  }
}

await copyTree(path.join(root, 'game'), output, (name, entry) => !(entry.isFile() && name === 'rabbit.obj'));
await fs.rm(path.join(output, 'assets', 'rabbit.obj'), { force: true });
await copyTree(path.join(root, 'music'), path.join(output, 'music'), name => name.endsWith('.mp3'));
console.log(`Android offline game assets ready: ${output}`);
