import { spawnSync } from 'node:child_process';

const gitCheck = spawnSync('git', ['rev-parse', '--is-inside-work-tree'], {
  stdio: 'ignore',
  shell: true,
});

// Skip Husky setup when the project isn't in a usable Git worktree.
if (gitCheck.status !== 0) {
  process.exit(0);
}

const huskyInstall = spawnSync('npx', ['husky', 'install'], {
  stdio: 'inherit',
  shell: true,
});

process.exit(huskyInstall.status ?? 0);
