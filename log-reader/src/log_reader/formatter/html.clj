(ns log-reader.formatter.html
  (:require [log-reader.formatter :as f]
            [log-reader.nodes-map :as nm]
            [zprint.core :as zp]))

#_(defn- ->hiccup [{:keys [callinfo children message] :as node}]
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

(defn- format-trace [{:keys [args fn id time] :as trace}]
  [:span.trace
   [:span.id id]
   [:span.time time]
   [:span.fn fn]
   [:span.args args]])

(defn- format-traces [traces]
  (map format-trace traces))

(defrecord HtmlFormatter []

  f/Formatter

  (format-line [this {:keys [data msg name ns time trace] :as line}]
    [:div.line
     [:span.time time]
     (into [:span.traces] (format-traces trace))
     [:span.name name]
     [:span.msg msg]
     [:span.data data]]))

(defn construct []
  (map->HtmlFormatter {}))
