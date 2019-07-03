(ns log-reader.formatter.text
  (:require [clojure.edn :as edn]
            #_[clojure.pprint :as pp]
            [clojure.string :as s]
            [log-reader.formatter :as f]
            [tick.alpha.api :as t]
            [zprint.core :as zp]))

(defonce RESET       "\u001b[0m")
(defonce BOLD        "\u001b[1m")
(defonce FG_BLACK    "\u001b[30m")
(defonce FG_RED      "\u001b[31m")
(defonce FG_GREEN    "\u001b[32m")
(defonce FG_YELLOW   "\u001b[33m")
(defonce FG_BLUE     "\u001b[34m")
(defonce FG_MAGENTA  "\u001b[35m")
(defonce FG_CYAN     "\u001b[36m")
(defonce FG_WHITE    "\u001b[37m")
(defonce FG_BBLACK   "\u001b[90m")
(defonce FG_BRED     "\u001b[91m")
(defonce FG_BGREEN   "\u001b[92m")
(defonce FG_BYELLOW  "\u001b[93m")
(defonce FG_BBLUE    "\u001b[94m")
(defonce FG_BMAGENTA "\u001b[95m")
(defonce FG_BCYAN    "\u001b[96m")
(defonce FG_BWHITE   "\u001b[97m")
(defonce BG_BLACK    "\u001b[40m")
(defonce BG_RED      "\u001b[41m")
(defonce BG_GREEN    "\u001b[42m")
(defonce BG_YELLOW   "\u001b[43m")
(defonce BG_BLUE     "\u001b[44m")
(defonce BG_MAGENTA  "\u001b[45m")
(defonce BG_CYAN     "\u001b[46m")
(defonce BG_WHITE    "\u001b[47m")
(defonce BG_BBLACK   "\u001b[100m")
(defonce BG_BRED     "\u001b[101m")
(defonce BG_BGREEN   "\u001b[102m")
(defonce BG_BYELLOW  "\u001b[103m")
(defonce BG_BBLUE    "\u001b[104m")
(defonce BG_BMAGENTA "\u001b[105m")
(defonce BG_BCYAN    "\u001b[106m")
(defonce BG_BWHITE   "\u001b[107m")

(defn- format-checkpoint [{:keys [args file fn id line name ns time] :as checkpoint}]
  (format "%s%s%s%s:%s%d%s"
          FG_WHITE BOLD name RESET
          FG_WHITE (mod id 1000) RESET))

(defn- format-checkpoints [checkpoints]
  #_(let [separator (format "%s%s-%s" FG_MAGENTA BOLD RESET)])
  (->> checkpoints
       (map format-checkpoint)
       (s/join " ")))

(defn- format-level [level]
  (let [[fg bg style] (case level
                        :trace [FG_WHITE   ""         ""]
                        :debug [FG_WHITE   ""         BOLD]
                        :info  [FG_CYAN    ""         BOLD]
                        :warn  [FG_YELLOW  ""         BOLD]
                        :error [FG_RED     ""         BOLD]
                        :fatal [FG_RED     BG_YELLOW  BOLD]
                        ["" "" ""])
        levelstr (or (some-> level name s/upper-case) "?")]
    (format "%s%s%s%-5s%s" bg fg style levelstr RESET)))

(defn- format-location [file fn line]
  (format "%s%s%s:%s%s%s%s:%s%s%s"
          FG_CYAN file RESET
          FG_CYAN BOLD fn RESET
          FG_CYAN line RESET))

(defn- format-message [msg]
  (if msg
    (format "%s%s%s%s" FG_GREEN BOLD msg RESET)
    ""))

(defn- format-data [data]
  (if (and data (not-empty data))
    (let [pretty-data #_(with-out-str (pp/pprint data)) (zp/czprint-str data)]
      (format "%s%s%s" BOLD pretty-data RESET))
    ""))

(defn- format-line [{:keys [checkpoints data level msg] :as line}]
  (let [{:keys [file fn id line name ns time]} (-> checkpoints last)
        checkpoints                            (-> checkpoints butlast format-checkpoints)
        utctime                                (or (and time
                                                        (t/format :iso-instant (t/instant time)))
                                                   "?")
        level                                  (format-level level)
        file                                   (or file "?")
        fn                                     (or fn "?")
        line                                   (or line -1)
        ;;;location                               (format-location file fn line)
        msg                                    (format-message msg)
        data                                   (format-data data)]
    (format "%s %-5s %s (%s) %s"
            utctime level msg checkpoints data)))

(defrecord TextFormatter []

  f/Formatter

  (format-line [this line]
    (format-line line)))

(defn construct []
  (map->TextFormatter {}))
