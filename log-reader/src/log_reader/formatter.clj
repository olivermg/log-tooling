(ns log-reader.formatter
  (:refer-clojure :rename {format format-clj}))

(defprotocol Formatter
  (format [this nodes-map]))
