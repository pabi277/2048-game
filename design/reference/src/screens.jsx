// SPDX-License-Identifier: MIT
//
// Every screen and state required by docs/UI-REFERENCE.md (rows 1-27), plus the
// two confirm dialogs and the Hindi Home run. One panel per row; the harness in
// App.jsx shows them one at a time inside a 390x844 frame.
import {
  AppBar,
  Banner,
  Board,
  Card,
  Chip,
  Dialog,
  DirPad,
  IconBtn,
  Icons,
  PrimaryBtn,
  RowGroup,
  Screen,
  ScoreBox,
  SecondaryBtn,
  SectionLabel,
  SegmentedChoice,
  Sheet,
  SettingsRow,
  Skeleton,
  StatTile,
  TextAction,
  Tile,
  ToggleRow,
  tx,
  useDesign,
} from './ui.jsx';
import { radius, size } from './design.js';

const fmt = (n) => n.toLocaleString('en-US');

const BOARDS = {
  fresh: [
    [0, 2, 0, 0],
    [0, 0, 0, 0],
    [0, 0, 4, 0],
    [0, 0, 0, 0],
  ],
  mid: [
    [2, 4, 8, 16],
    [32, 64, 128, 256],
    [512, 1024, 2, 0],
    [0, 8, 32, 64],
  ],
  win: [
    [2048, 1024, 512, 256],
    [4, 8, 16, 32],
    [2, 4, 8, 16],
    [0, 2, 0, 4],
  ],
  over: [
    [2, 4, 8, 16],
    [16, 8, 4, 2],
    [2, 4, 8, 16],
    [16, 8, 4, 2],
  ],
};

/* ------------------------------------------------------------------- home */

function Hero({ dense = false }) {
  const { t, scale } = useDesign();
  return (
    <div style={{ paddingTop: dense ? 8 : 24 }}>
      <div style={tx('appTitle', scale, { color: 'var(--text)' })}>{t('appName')}</div>
      <div style={tx('body', scale, { color: 'var(--text-secondary)', marginTop: 4 })}>{t('tagline')}</div>
    </div>
  );
}

function ResumeCard() {
  const { t, scale } = useDesign();
  return (
    <Card style={{ marginTop: 24 }}>
      <div className="flex items-center justify-between gap-2">
        <div style={tx('scoreLabel', scale, { textTransform: 'uppercase', letterSpacing: `${0.08 * scale}em`, color: 'var(--text-secondary)' })}>
          {t('gameInProgress')}
        </div>
        <Chip tone="outline">{t('dailyChallenge')}</Chip>
      </div>
      <div style={tx('scoreValue', scale, { marginTop: 8, fontVariantNumeric: 'tabular-nums' })}>{fmt(8412)}</div>
      <div style={tx('secondary', scale, { color: 'var(--text-secondary)' })}>
        {t('resumeMeta', fmt(8412), 512)}
      </div>
      <div style={tx('caption', scale, { color: 'var(--text-secondary)', marginTop: 8 })}>{t('continueHint')}</div>
    </Card>
  );
}

function DailyCard() {
  const { t, scale } = useDesign();
  return (
    <Card style={{ marginTop: 24 }}>
      <div className="flex items-center justify-between gap-2">
        <div style={tx('scoreLabel', scale, { textTransform: 'uppercase', letterSpacing: `${0.08 * scale}em`, color: 'var(--text-secondary)' })}>
          {t('dailyLabel')}
        </div>
        <Chip tone="accent">{t('streak', 4)}</Chip>
      </div>
      <div style={tx('secondary', scale, { color: 'var(--text-secondary)', marginTop: 8 })}>{t('todaysScore')}</div>
      <div style={tx('scoreValue', scale, { fontVariantNumeric: 'tabular-nums' })}>{fmt(1620)}</div>
    </Card>
  );
}

function HomeScreen({ variant = 'no-save', banner = null }) {
  const { t, scale } = useDesign();
  const inProgress = variant === 'in-progress';
  const dailyDone = variant === 'daily-done';
  const empty = variant === 'empty';

  return (
    <Screen>
      {banner ? <div style={{ marginBottom: 12 }}>{banner}</div> : null}
      <Hero dense={Boolean(banner)} />
      <div style={{ flex: 1, minHeight: 16 }} />
      {inProgress ? <ResumeCard /> : null}
      {dailyDone ? <DailyCard /> : null}

      <div className="flex flex-col" style={{ gap: 8, marginTop: 24 }}>
        <PrimaryBtn icon={inProgress ? Icons.play : Icons.play}>
          {inProgress ? t('continueGame') : t('newGame')}
        </PrimaryBtn>
        <SecondaryBtn disabled={dailyDone}>{dailyDone ? t('dailyDone') : t('dailyChallenge')}</SecondaryBtn>
        {inProgress ? <TextAction tone="muted">{t('newGame')}</TextAction> : null}
      </div>

      <div className="flex flex-wrap items-center justify-center" style={{ gap: 0, marginTop: 16 }}>
        <TextAction tone="muted">{t('howToPlay')}</TextAction>
        <span style={{ color: 'var(--text-secondary)' }}>·</span>
        <TextAction tone="muted">{t('stats')}</TextAction>
        <span style={{ color: 'var(--text-secondary)' }}>·</span>
        <TextAction tone="muted">{t('settings')}</TextAction>
      </div>

      <div style={{ flex: 1, minHeight: 8 }} />
      <div
        className="text-center"
        style={tx('caption', scale, { color: 'var(--text-secondary)', paddingBottom: 8 })}
      >
        {empty ? t('noBestYet') : t('homeBestLine', fmt(12480))}
      </div>
      {empty ? (
        <div className="text-center" style={tx('caption', scale, { color: 'var(--text-secondary)' })}>
          {t('startHint')}
        </div>
      ) : null}
    </Screen>
  );
}

function LoadingScreen() {
  const { t, scale } = useDesign();
  return (
    <Screen>
      <div style={{ flex: 1 }} />
      <div className="flex items-center justify-center" style={{ gap: 6 }}>
        {[2, 4, 8, 16].map((v) => (
          <div key={v} style={{ width: 44, height: 44 }}>
            <Tile value={v} />
          </div>
        ))}
      </div>
      <div className="text-center" style={tx('screenTitle', scale, { marginTop: 16 })}>
        {t('appName')}
      </div>
      <div className="text-center" style={tx('secondary', scale, { color: 'var(--text-secondary)', marginTop: 4 })}>
        {t('tagline')}
      </div>
      <div style={{ flex: 1 }} />
      <div
        style={{
          background: 'var(--board)',
          borderRadius: radius.board,
          padding: size.boardPadding,
          display: 'grid',
          gridTemplateColumns: 'repeat(4, 1fr)',
          gap: size.tileGap,
        }}
      >
        {Array.from({ length: 16 }).map((_, i) => (
          <Skeleton key={i} style={{ borderRadius: radius.tile, aspectRatio: '1 / 1', opacity: i % 3 === 0 ? 0.6 : 1 }} />
        ))}
      </div>
      <div style={{ flex: 1 }} />
    </Screen>
  );
}

/* ------------------------------------------------------------------- game */

function GameScreen({ grid, score, best, hint = false, dim = false, popping = null, banner = null, pad = false }) {
  const { t, scale } = useDesign();
  return (
    <Screen>
      <div className="flex items-center gap-1">
        <div className="min-w-0 flex-1" style={tx('screenTitle', scale)}>
          {t('appName')}
        </div>
        <IconBtn icon={Icons.home} label={t('home')} />
        <IconBtn icon={Icons.restart} label={t('restart')} />
      </div>

      <div className="flex" style={{ gap: size.scoreGap, marginTop: 4 }}>
        <ScoreBox label={t('score')} value={fmt(score)} />
        <ScoreBox label={t('best')} value={fmt(best)} />
      </div>

      {banner ? <div style={{ marginTop: 12 }}>{banner}</div> : null}

      <div style={{ marginTop: size.boardControlsGap }}>
        <Board grid={grid} dim={dim} popping={popping} />
      </div>

      <div className="flex items-center justify-between" style={{ gap: size.controlGap, marginTop: 12 }}>
        <TextAction icon={Icons.undo} tone="muted">
          {t('undo')}
        </TextAction>
        {hint ? <Chip tone="outline">{t('swipeHint')}</Chip> : null}
        <IconBtn icon={Icons.up} label={t('directionPad')} />
      </div>

      {pad ? (
        <div className="flex justify-center" style={{ marginTop: 8 }}>
          <DirPad />
        </div>
      ) : null}
      <div style={{ height: 8 }} />
    </Screen>
  );
}

function PausedScreen() {
  const { t, scale } = useDesign();
  return (
    <div className="relative h-full">
      <GameScreen grid={BOARDS.mid} score={8412} best={12480} dim />
      <Sheet
        title={t('paused')}
        actions={
          <>
            <PrimaryBtn icon={Icons.play}>{t('resume')}</PrimaryBtn>
            <SecondaryBtn icon={Icons.restart}>{t('restart')}</SecondaryBtn>
            <TextAction tone="muted" icon={Icons.home}>
              {t('home')}
            </TextAction>
          </>
        }
      >
        <div style={tx('caption', scale, { color: 'var(--text-secondary)', textAlign: 'center' })}>
          {t('resumeMeta', fmt(8412), 512)}
        </div>
      </Sheet>
    </div>
  );
}

function WinScreen() {
  const { t, scale } = useDesign();
  return (
    <div className="relative h-full">
      <GameScreen grid={BOARDS.win} score={21044} best={21044} dim />
      <Dialog
        title={t('winTitle')}
        actions={
          <>
            <PrimaryBtn>{t('keepPlaying')}</PrimaryBtn>
            <SecondaryBtn>{t('newGame')}</SecondaryBtn>
          </>
        }
      >
        <div style={tx('secondary', scale, { color: 'var(--text-secondary)' })}>{t('winBody')}</div>
        <div className="flex" style={{ gap: 12, marginTop: 16 }}>
          <div className="flex-1">
            <ScoreBox label={t('score')} value={fmt(21044)} scaleCap={1.25} />
          </div>
          <div className="flex-1">
            <ScoreBox label={t('best')} value={fmt(21044)} scaleCap={1.25} />
          </div>
        </div>
      </Dialog>
    </div>
  );
}

function GameOverScreen({ newBest = true }) {
  const { t, scale } = useDesign();
  return (
    <div className="relative h-full">
      <GameScreen grid={BOARDS.over} score={3120} best={newBest ? 3120 : 12480} dim />
      <Dialog
        title={t('gameOver')}
        actions={
          <>
            <PrimaryBtn icon={Icons.undo}>{t('undoLastMove')}</PrimaryBtn>
            <SecondaryBtn>{t('newGame')}</SecondaryBtn>
            <TextAction tone="muted" icon={Icons.home}>
              {t('home')}
            </TextAction>
          </>
        }
      >
        <div className="flex items-center gap-2">
          <div style={tx('secondary', scale, { color: 'var(--text-secondary)' })}>{t('finalScore')}</div>
          {newBest ? <Chip tone="success">{t('newBest')}</Chip> : null}
        </div>
        <div className="flex" style={{ gap: 12, marginTop: 8 }}>
          <div className="flex-1">
            <ScoreBox label={t('score')} value={fmt(3120)} scaleCap={1.25} />
          </div>
          <div className="flex-1">
            <ScoreBox label={t('best')} value={fmt(newBest ? 3120 : 12480)} scaleCap={1.25} />
          </div>
        </div>
        <div style={tx('caption', scale, { color: 'var(--text-secondary)', marginTop: 12 })}>{t('freeUndo')}</div>
      </Dialog>
    </div>
  );
}

function GameOverDismissedScreen() {
  const { t, scale } = useDesign();
  return (
    <Screen>
      <GameScreen grid={BOARDS.over} score={3120} best={3120} />
      <Card style={{ borderStyle: 'dashed', background: 'transparent', marginBottom: 8 }}>
        <div style={tx('caption', scale, { color: 'var(--text-secondary)' })}>
          <strong>Reference note · phase 5 only.</strong> After the game-over dialog is dismissed, an
          interstitial may appear here — at most one per three game-overs, 120 s apart, never for
          ad-free users. This reference shows the suppressed case: <em>no ad art in the design</em>.
        </div>
      </Card>
    </Screen>
  );
}

/* ---------------------------------------------------------------- how to */

function NumberedRule({ n, children }) {
  const { scale } = useDesign();
  return (
    <div className="flex gap-3" style={{ marginBottom: 12 }}>
      <div
        className="flex shrink-0 items-center justify-center"
        style={{
          width: 24,
          height: 24,
          borderRadius: 12,
          background: 'var(--accent)',
          color: 'var(--on-accent)',
          ...tx('caption', scale, { fontWeight: 700 }),
        }}
      >
        {n}
      </div>
      <div style={tx('body', scale)}>{children}</div>
    </div>
  );
}

function ExampleRow() {
  const { scale } = useDesign();
  return (
    <div className="flex items-center" style={{ gap: 8 }}>
      <div style={{ width: 48, height: 48 }}>
        <Tile value={8} />
      </div>
      <span style={tx('body', scale, { color: 'var(--text-secondary)' })}>+</span>
      <div style={{ width: 48, height: 48 }}>
        <Tile value={8} />
      </div>
      <span style={tx('body', scale, { color: 'var(--text-secondary)' })}>=</span>
      <div style={{ width: 48, height: 48 }}>
        <Tile value={16} />
      </div>
      <span style={tx('secondary', scale, { color: 'var(--accent)', fontWeight: 600, marginLeft: 4 })}>+16</span>
    </div>
  );
}

function HowToPlayScreen() {
  const { t, scale } = useDesign();
  const rules = t('rules');
  return (
    <Screen>
      <AppBar title={t('howToPlay')} />
      <div className="flex flex-col" style={{ marginTop: 8 }}>
        {rules.map((rule, i) => (
          <NumberedRule key={i} n={i + 1}>
            {rule}
          </NumberedRule>
        ))}
      </div>
      <SectionLabel>{t('scoringTitle')}</SectionLabel>
      <Card>
        <ExampleRow />
        <div style={tx('secondary', scale, { color: 'var(--text-secondary)', marginTop: 12 })}>{t('scoringBody')}</div>
      </Card>
      <SectionLabel>{t('undoTitle')}</SectionLabel>
      <Card>
        <div style={tx('secondary', scale, { color: 'var(--text-secondary)' })}>{t('undoBody')}</div>
      </Card>
      <SectionLabel>{t('controlsTitle')}</SectionLabel>
      <Card style={{ marginBottom: 16 }}>
        <div style={tx('secondary', scale, { color: 'var(--text-secondary)', marginBottom: 12 })}>
          {t('controlsBody')}
        </div>
        <div className="flex justify-center">
          <DirPad />
        </div>
      </Card>
    </Screen>
  );
}

/* -------------------------------------------------------------- settings */

function SettingsScreen({ variant = 'content' }) {
  const { t, scale } = useDesign();
  const adFree = variant === 'ad-free';
  const unavailable = variant === 'unavailable';
  return (
    <Screen>
      <AppBar title={t('settings')} />

      <SectionLabel>{t('secPlay')}</SectionLabel>
      <RowGroup>
        <ToggleRow title={t('sound')} subtitle={t('soundOn')} checked onClick={() => {}} first />
        <ToggleRow title={t('haptics')} subtitle={t('hapticsOn')} checked onClick={() => {}} />
        <ToggleRow title={t('showDirectionPad')} checked={false} onClick={() => {}} />
      </RowGroup>

      <SectionLabel>{t('secLook')}</SectionLabel>
      <SegmentedChoice
        value="system"
        onChange={() => {}}
        options={[
          { value: 'system', label: t('themeSystem') },
          { value: 'light', label: t('themeLight') },
          { value: 'dark', label: t('themeDark') },
        ]}
      />
      <RowGroup style={{ marginTop: 8 }}>
        <SettingsRow
          title={t('themeGold')}
          subtitle={t('goldLocked')}
          disabled
          first
          trail={<span style={{ color: 'var(--text-secondary)' }}><Icons.lock size={18} strokeWidth={2} /></span>}
        />
      </RowGroup>
      <div style={{ height: 8 }} />
      <SegmentedChoice
        value="en"
        onChange={() => {}}
        options={[
          { value: 'en', label: 'English' },
          { value: 'hi', label: 'हिन्दी' },
        ]}
      />

      <SectionLabel>{t('secProgress')}</SectionLabel>
      <RowGroup>
        <SettingsRow
          title={t('resetStats')}
          subtitle={t('resetStatsSub')}
          destructive
          trail={
            <span style={{ color: 'var(--text-secondary)' }}>
              <Icons.chevron size={18} strokeWidth={2} />
            </span>
          }
        />
      </RowGroup>

      <SectionLabel>{t('secAds')}</SectionLabel>
      <RowGroup>
        {adFree ? (
          <SettingsRow
            title={t('adFreeActive')}
            subtitle={t('adFreeSub')}
            disabled
            trail={<span style={{ color: 'var(--success)' }}>✓</span>}
          />
        ) : unavailable ? (
          <>
            <SettingsRow title={t('removeAds')} subtitle={t('storeUnavailable')} disabled />
            <div style={{ padding: '0 16px 12px' }}>
              <div style={tx('caption', scale, { color: 'var(--text-secondary)' })}>{t('rewardedHidden')}</div>
            </div>
          </>
        ) : (
          <SettingsRow
            title={t('removeAds')}
            subtitle={t('removeAdsSub')}
            trail={
              <span style={{ color: 'var(--text-secondary)' }}>
                <Icons.chevron size={18} strokeWidth={2} />
              </span>
            }
          />
        )}
      </RowGroup>

      <SectionLabel>{t('secAbout')}</SectionLabel>
      <RowGroup style={{ marginBottom: 16 }}>
        <SettingsRow title={t('version')} disabled first />
        <SettingsRow
          title={t('licences')}
          trail={<span style={{ color: 'var(--text-secondary)' }}><Icons.chevron size={18} strokeWidth={2} /></span>}
        />
        <SettingsRow
          title={t('privacy')}
          trail={<span style={{ color: 'var(--text-secondary)' }}><Icons.chevron size={18} strokeWidth={2} /></span>}
        />
      </RowGroup>
    </Screen>
  );
}

/* ----------------------------------------------------------------- stats */

function StatsGrid({ items, columns }) {
  return (
    <div style={{ display: 'grid', gridTemplateColumns: `repeat(${columns}, 1fr)`, gap: 12 }}>
      {items.map((item) => (
        <StatTile key={item.label} {...item} />
      ))}
    </div>
  );
}

function StatsScreen({ variant = 'populated', banner = null }) {
  const { t, scale } = useDesign();
  const empty = variant === 'empty';
  const unknownVariant = variant === 'unknown';
  const unknown = t('unknown');
  const columns = scale >= 2 ? 1 : 2;
  const items = [
    { label: t('gamesPlayed'), value: empty || unknownVariant ? unknown : '128' },
    { label: t('wins'), value: empty || unknownVariant ? unknown : '14' },
    { label: t('bestScore'), value: empty || unknownVariant ? unknown : '12,480' },
    { label: t('highestTile'), value: empty || unknownVariant ? unknown : '1024' },
    { label: t('dailyStreak'), value: empty || unknownVariant ? unknown : '4' },
    { label: t('bestDaily'), value: empty || unknownVariant ? unknown : '3,240' },
  ];
  return (
    <Screen>
      <AppBar title={t('stats')} />
      {banner ? <div style={{ marginBottom: 16 }}>{banner}</div> : null}
      {empty ? (
        <>
          <div className="flex items-center gap-2">
            <div style={tx('screenTitle', scale)}>{t('noGamesYet')}</div>
          </div>
          <div style={tx('secondary', scale, { color: 'var(--text-secondary)', marginBottom: 16 })}>
            {t('noGamesBody')}
          </div>
          <PrimaryBtn icon={Icons.play}>{t('newGame')}</PrimaryBtn>
        </>
      ) : null}
      <div style={{ marginTop: empty ? 24 : 8 }}>
        <StatsGrid items={items} columns={columns} />
      </div>
    </Screen>
  );
}

/* ---------------------------------------------------------------- panels */

const P = (id, n, group, title, opts) => ({ id, n, group, title, ...opts });

export const panels = [
  P('home-no-save', 1, 'Home', 'Home — content (no saved game)', {
    theme: 'light',
    uses: ['HomeScreen', 'PrimaryButton', 'SecondaryButton', 'TextLinkRow', 'BestScoreLine'],
    check: 'Big title, one-line subtitle, primary/secondary/links, best line last.',
    render: () => <HomeScreen variant="no-save" />,
  }),
  P('home-in-progress', 2, 'Home', 'Home — game in progress', {
    theme: 'light',
    uses: ['HomeScreen', 'ResumeCard', 'ConfirmDialog'],
    check: 'Continue is primary; New game demoted to a text action (confirm on tap).',
    render: () => <HomeScreen variant="in-progress" />,
  }),
  P('home-daily-done', 3, 'Home', 'Home — daily completed today', {
    theme: 'light',
    uses: ['DailyCard', 'StreakChip'],
    check: "Today's score, streak chip, disabled 'come back tomorrow'.",
    render: () => <HomeScreen variant="daily-done" />,
  }),
  P('home-empty', 4, 'Home', 'Home — empty / never played', {
    theme: 'light',
    uses: ['HomeScreen'],
    check: 'Best reads "—", no stats teaser, no fake zeroes.',
    render: () => <HomeScreen variant="empty" />,
  }),
  P('home-storage-error', 5, 'Home', 'Home — storage error', {
    theme: 'light',
    uses: ['InlineBanner'],
    check: 'Non-blocking banner; New game still works.',
    render: () => (
      <HomeScreen
        variant="no-save"
        banner={
          <Banner message="Couldn't read your saved game" detail="Your last board may be missing." onRetry={() => {}} />
        }
      />
    ),
  }),
  P('home-loading', 6, 'Home', 'Home — loading (one frame)', {
    theme: 'light',
    uses: ['SplashScreen', 'BoardSkeleton'],
    check: 'One frame only: mark + skeleton board, never a long splash.',
    render: () => <LoadingScreen />,
  }),

  P('game-new', 7, 'Game', 'Game — new game (playing)', {
    theme: 'light',
    uses: ['GameScreen', 'ScoreBox ×2', 'BoardFrame', 'TileGrid', 'IconButtonRow', 'DirectionPad', 'UndoButton'],
    check: 'Home+restart above the board, taupe SCORE/BEST, swipe hint visible, pad off.',
    render: () => <GameScreen grid={BOARDS.fresh} score={0} best={12480} hint />,
  }),
  P('game-mid', 8, 'Game', 'Game — mid-game (playing)', {
    theme: 'light',
    uses: ['GameScreen', 'Tile (animated)'],
    check: 'Full ramp visible (2 → 1024), one tile mid-merge, hint already faded.',
    render: () => <GameScreen grid={BOARDS.mid} score={8412} best={12480} popping="1-1" />,
  }),
  P('game-paused', 9, 'Game', 'Game — paused', {
    theme: 'light',
    uses: ['PauseSheet'],
    check: 'Board dimmed, sheet from the bottom: Resume / Restart / Home.',
    render: () => <PausedScreen />,
  }),
  P('game-dark', 10, 'Game', 'Game — dark', {
    theme: 'dark',
    uses: ['GameScreen (dark)'],
    check: 'Dark ramp, same hierarchy, no pure-black tiles.',
    render: () => <GameScreen grid={BOARDS.mid} score={8412} best={12480} />,
  }),
  P('game-large-font', 11, 'Game', 'Game — large font (2.0×)', {
    theme: 'light',
    scale: 2,
    uses: ['GameScreen (fontScale)'],
    check: 'Score boxes wrap to two lines, board stays square, targets ≥48dp, numerals stay legible.',
    render: () => <GameScreen grid={BOARDS.mid} score={8412} best={12480} />,
  }),
  P('game-storage-error', 12, 'Game', 'Game — storage error', {
    theme: 'light',
    uses: ['InlineBanner'],
    check: 'Banner above the board, play continues, Retry present.',
    render: () => (
      <GameScreen
        grid={BOARDS.mid}
        score={8412}
        best={12480}
        banner={<Banner message="Progress isn't saving on this device" detail="Playing on is safe." onRetry={() => {}} />}
      />
    ),
  }),

  P('win', 13, 'Dialogs', 'Win — 2048 reached', {
    theme: 'light',
    uses: ['WinDialog'],
    check: 'Keep playing primary, New game secondary, score+best inside.',
    render: () => <WinScreen />,
  }),
  P('win-dark-large', 14, 'Dialogs', 'Win — dark / large font', {
    theme: 'dark',
    scale: 2,
    uses: ['WinDialog'],
    check: 'Dialog scrolls instead of clipping at 2.0×; buttons stay ≥48dp.',
    render: () => <WinScreen />,
  }),
  P('game-over', 15, 'Dialogs', 'Game over', {
    theme: 'light',
    uses: ['GameOverDialog', 'NewBestChip', 'RewardedUndoButton'],
    check: 'New best chip, undo last move, New game, Home.',
    render: () => <GameOverScreen />,
  }),
  P('game-over-dismissed', 16, 'Dialogs', 'Game over — after dismissal (ad slot)', {
    theme: 'light',
    uses: ['AdSlotPlaceholder (reference only)'],
    check: 'Suppressed case by default; no ad art in this design.',
    render: () => <GameOverDismissedScreen />,
  }),
  P('game-over-dark-large', 17, 'Dialogs', 'Game over — dark / large font', {
    theme: 'dark',
    scale: 2,
    uses: ['GameOverDialog'],
    check: 'Same content at 2.0× in dark; nothing clipped.',
    render: () => <GameOverScreen />,
  }),

  P('how-to-play', 18, 'How to play', 'How to play', {
    theme: 'light',
    uses: ['HowToPlayScreen', 'NumberedRule', 'ExampleRow'],
    check: 'Seven rules, scoring example, undo, controls.',
    render: () => <HowToPlayScreen />,
  }),
  P('how-to-play-dark-large', 19, 'How to play', 'How to play — dark / large font (Hindi)', {
    theme: 'dark',
    scale: 2,
    lang: 'hi',
    uses: ['HowToPlayScreen'],
    check: 'Longest Hindi run at 2.0× — scrolls, never truncates.',
    render: () => <HowToPlayScreen />,
  }),

  P('settings', 20, 'Settings', 'Settings — content', {
    theme: 'light',
    uses: ['SettingsScreen', 'SettingsRow', 'SwitchRow', 'SegmentedChoice', 'LockedThemeRow', 'PurchaseRow'],
    check: 'All sections present; Gold locked with a reason; no price invented.',
    render: () => <SettingsScreen />,
  }),
  P('settings-ad-free', 21, 'Settings', 'Settings — ad-free active', {
    theme: 'light',
    uses: ['SettingsScreen'],
    check: 'Purchase row becomes an "Ad-free" state; rewarded entries gone.',
    render: () => <SettingsScreen variant="ad-free" />,
  }),
  P('settings-unavailable', 22, 'Settings', 'Settings — purchase unavailable', {
    theme: 'light',
    uses: ['SettingsScreen (error variant)'],
    check: 'Disabled row with a reason and a quiet note; nothing looks broken.',
    render: () => <SettingsScreen variant="unavailable" />,
  }),
  P('settings-dark-large', 23, 'Settings', 'Settings — dark / large font (Hindi)', {
    theme: 'dark',
    scale: 2,
    lang: 'hi',
    uses: ['SettingsScreen'],
    check: 'Rows grow, Hindi labels never truncate, switches stay 48dp.',
    render: () => <SettingsScreen />,
  }),

  P('stats-empty', 24, 'Stats', 'Stats — empty', {
    theme: 'light',
    uses: ['StatsScreen (empty)', 'EmptyState'],
    check: '"No games yet" + CTA; values read "—".',
    render: () => <StatsScreen variant="empty" />,
  }),
  P('stats-populated', 25, 'Stats', 'Stats — populated', {
    theme: 'light',
    uses: ['StatsScreen', 'StatTile'],
    check: 'Six facts, two columns, no charts.',
    render: () => <StatsScreen variant="populated" />,
  }),
  P('stats-dark-large', 26, 'Stats', 'Stats — dark / large font', {
    theme: 'dark',
    scale: 2,
    uses: ['StatsScreen'],
    check: 'Grid reflows to one column at 2.0×.',
    render: () => <StatsScreen variant="populated" />,
  }),
  P('stats-storage-error', 27, 'Stats', 'Stats — storage error', {
    theme: 'light',
    uses: ['InlineBanner'],
    check: 'Banner + unknown values; zeros are not pretended.',
    render: () => (
      <StatsScreen
        variant="unknown"
        banner={
          <Banner
            message="Couldn't read your stats"
            detail="Blanks below are unknown, not zero."
            onRetry={() => {}}
          />
        }
      />
    ),
  }),

  P('confirm-restart', 28, 'Dialogs', 'Confirm — restart game', {
    theme: 'light',
    uses: ['ConfirmDialog'],
    check: 'Destructive confirm: Cancel first, restart second? (review) ',
    render: () => (
      <div className="relative h-full">
        <GameScreen grid={BOARDS.mid} score={8412} best={12480} dim />
        <ConfirmRestart />
      </div>
    ),
  }),
  P('confirm-reset-stats', 29, 'Dialogs', 'Confirm — reset stats', {
    theme: 'light',
    uses: ['ConfirmDialog'],
    check: 'Spells out exactly what is erased.',
    render: () => (
      <div className="relative h-full">
        <SettingsScreen />
        <ConfirmReset />
      </div>
    ),
  }),
  P('home-hindi', 30, 'Language', 'Home — Hindi (length run)', {
    theme: 'light',
    lang: 'hi',
    uses: ['HomeScreen'],
    check: 'Same layout, Hindi copy; watch the link row and button widths.',
    render: () => <HomeScreen variant="no-save" />,
  }),
];

function ConfirmRestart() {
  const { t, scale } = useDesign();
  return (
    <Dialog
      title={t('restartTitle')}
      actions={
        <>
          <PrimaryBtn>{t('restart')}</PrimaryBtn>
          <SecondaryBtn>{t('cancel')}</SecondaryBtn>
        </>
      }
    >
      <div style={tx('secondary', scale, { color: 'var(--text-secondary)' })}>{t('restartBody')}</div>
    </Dialog>
  );
}

function ConfirmReset() {
  const { t, scale } = useDesign();
  return (
    <Dialog
      title={t('resetStatsTitle')}
      actions={
        <>
          <PrimaryBtn>{t('reset')}</PrimaryBtn>
          <SecondaryBtn>{t('cancel')}</SecondaryBtn>
        </>
      }
    >
      <div style={tx('secondary', scale, { color: 'var(--text-secondary)' })}>{t('resetStatsBody')}</div>
    </Dialog>
  );
}

export const panelGroups = ['Home', 'Game', 'Dialogs', 'How to play', 'Settings', 'Stats', 'Language'];
