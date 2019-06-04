(ns log-reader.formatter)

(defprotocol Formatter
  (format-line [this line]))

(defn format-lines-xf [this]
  (map (partial format-line this)))
