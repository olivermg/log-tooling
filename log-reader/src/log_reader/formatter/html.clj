(ns log-reader.formatter.html
  (:require [log-reader.formatter :as f]
            [zprint.core :as zp]))

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
