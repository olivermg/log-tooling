(ns log-reader.formatter.text
  (:require [clojure.edn :as edn]
            [clojure.string :as s]
            [log-reader.formatter :as f]
            [tick.alpha.api :as t]
            [zprint.core :as zp]))

(defn- format-checkpoint [{:keys [args file fn id line name ns time] :as checkpoint}]
  (str name ":" (mod id 1000)))

(defn- format-checkpoints [checkpoints]
  (->> (butlast checkpoints)
       (map format-checkpoint)
       (s/join " ")))

(defrecord TextFormatter []

  f/Formatter

  (format-line [this {:keys [checkpoints data level msg] :as line}]
    (let [{:keys [file fn id line name ns time]} (-> checkpoints last)
          utctime  (or (and time
                            (t/format :iso-instant (t/instant time)))
                       "?")
          level    (or (some-> level clojure.core/name s/upper-case) "?")
          file     (or file "?")
          fn       (or fn "?")
          line     (or line -1)
          metainfo (format "[%s] %-5s (%s) <%s:%s:%d>"
                           utctime level (format-checkpoints checkpoints) file fn line)]
      (->> [metainfo msg data]
           (remove nil?)
           (s/join ", ")))))

(defn construct []
  (map->TextFormatter {}))
