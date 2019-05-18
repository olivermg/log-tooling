(ns log-reader.formatter.html
  (:refer-clojure :rename {format format-clj})
  (:require [log-reader.formatter :as f]
            [log-reader.nodes-map :as nm]
            [zprint.core :as zp]))

(defn- ->hiccup [{:keys [callinfo children message] :as node}]
  (let [{:keys [args fn time]} callinfo]
    [:div.call
     [:span.fn   (zp/zprint-str fn)]
     (when message
       (let [{:keys [name ns time msg data]} message]
         [:span.message
          [:span.message-name (str name)]
          [:span.message-ns (str ns)]
          [:span.message-time (zp/zprint-str time)]
          [:span.message-msg (str msg)]
          [:span.message-data (zp/zprint-str data)]]))
     [:span.time (zp/zprint-str time)]
     [:span.args.collapsable.collapsed
      [:span.shown (zp/zprint-str args)]
      [:span.hidden "(args...)"]]
     (when children
       (into [:div.children]
             children))]))

(defrecord HtmlFormatter []

  f/Formatter

  (format [this nodes-map]
    (let [nodes (nm/sorted-data nodes-map)]
      (into [:div.log-output]
            (nm/traverse-all ->hiccup nodes)))))

(defn construct []
  (map->HtmlFormatter {}))
