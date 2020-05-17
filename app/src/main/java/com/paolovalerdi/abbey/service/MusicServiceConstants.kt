package com.paolovalerdi.abbey.service

const val TAG = "AbbeyMusicService"

const val MUSIC_PLAYER_PACKAGE_NAME = "com.paolovalerdi.abbey"
const val MUSIC_PACKAGE_NAME = "com.android.music"

const val ACTION_TOGGLE_PAUSE = "$MUSIC_PLAYER_PACKAGE_NAME.togglepause"
const val ACTION_PLAY = "$MUSIC_PLAYER_PACKAGE_NAME.play"
const val ACTION_PLAY_PLAYLIST = "$MUSIC_PLAYER_PACKAGE_NAME.play.playlist"
const val ACTION_PAUSE = "$MUSIC_PLAYER_PACKAGE_NAME.pause"
const val ACTION_STOP = "$MUSIC_PLAYER_PACKAGE_NAME.stop"
const val ACTION_SKIP = "$MUSIC_PLAYER_PACKAGE_NAME.skip"
const val ACTION_REWIND = "$MUSIC_PLAYER_PACKAGE_NAME.rewind"
const val ACTION_QUIT = "$MUSIC_PLAYER_PACKAGE_NAME.quitservice"
const val ACTION_PENDING_QUIT = "$MUSIC_PLAYER_PACKAGE_NAME.pendingquitservice"
const val INTENT_EXTRA_PLAYLIST = MUSIC_PLAYER_PACKAGE_NAME + "intentextra.playlist"
const val INTENT_EXTRA_SHUFFLE_MODE = "$MUSIC_PLAYER_PACKAGE_NAME.intentextra.shufflemode"

const val APP_WIDGET_UPDATE = "$MUSIC_PLAYER_PACKAGE_NAME.appwidgetupdate"
const val EXTRA_APP_WIDGET_NAME = MUSIC_PLAYER_PACKAGE_NAME + "app_widget_name"

// Do not change these three strings as it will break support with other apps (e.g. last.fm scrobbling)
const val META_CHANGED = "$MUSIC_PLAYER_PACKAGE_NAME.metachanged"
const val QUEUE_CHANGED = "$MUSIC_PLAYER_PACKAGE_NAME.queuechanged"
const val PLAY_STATE_CHANGED = "$MUSIC_PLAYER_PACKAGE_NAME.playstatechanged"

const val REPEAT_MODE_CHANGED = "$MUSIC_PLAYER_PACKAGE_NAME.repeatmodechanged"
const val SHUFFLE_MODE_CHANGED = "$MUSIC_PLAYER_PACKAGE_NAME.shufflemodechanged"
const val MEDIA_STORE_CHANGED = "$MUSIC_PLAYER_PACKAGE_NAME.mediastorechanged"

const val CYCLE_REPEAT = "$MUSIC_PLAYER_PACKAGE_NAME.cyclerepeat"
const val TOGGLE_SHUFFLE = "$MUSIC_PLAYER_PACKAGE_NAME.toggleshuffle"
const val TOGGLE_FAVORITE = "$MUSIC_PLAYER_PACKAGE_NAME.togglefavorite"

const val SAVED_POSITION = "POSITION"
const val SAVED_POSITION_IN_TRACK = "POSITION_IN_TRACK"
const val SAVED_SHUFFLE_MODE = "SHUFFLE_MODE"
const val SAVED_REPEAT_MODE = "REPEAT_MODE"

const val RELEASE_WAKELOCK = 0
const val TRACK_ENDED = 1
const val TRACK_WENT_TO_NEXT = 2
const val PLAY_SONG = 3
const val PREPARE_NEXT = 4
const val SET_POSITION = 5
const val FOCUS_CHANGE = 6
const val DUCK = 7
const val UNDUCK = 8
const val RESTORE_QUEUES = 9

const val SHUFFLE_MODE_NONE = 0
const val SHUFFLE_MODE_SHUFFLE = 1

const val REPEAT_MODE_NONE = 0
const val REPEAT_MODE_ALL = 1
const val REPEAT_MODE_THIS = 2

const val SAVE_QUEUES = 0