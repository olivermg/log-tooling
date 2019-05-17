(ns log-reader.webserver
  (:require [clojure.java.io :as io]
            [hiccup.core :as h]
            [org.httpkit.server :as hs]))

(defn- build-document [content]
  (->> [:html
        [:head
         [:title "log-reader"]
         [:link {:rel "stylesheet" :href "app.css"}]]
        [:body
         content
         [:script {:src "https://code.jquery.com/jquery-3.4.1.min.js"
                   :integrity "sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
                   :crossorigin "anonymous"}]
         [:script {:src "app.js"}]]]
       h/html
       (str "<!DOCTYPE HTML>")))

(defn- load-public-resource [path]
  (-> (io/resource (str "public" path))
      slurp))

(defn- build-app-js []
  (load-public-resource "/js/app.js"))

(defn- build-app-css []
  (load-public-resource "/css/app.css"))

(defn- make-handler [content]
  (let [document-response {:status  200
                           :headers {"Content-type" "text/html"}
                           :body    (build-document content)}
        app-js-response   {:status  200
                           :headers {"Content-type" "text/javascript"}
                           :body    (build-app-js)}
        app-css-response  {:status  200
                           :headers {"Content-type" "text/css"}
                           :body    (build-app-css)}]
    (fn [{:keys [uri] :as request}]
      (case uri
        "/"        document-response
        "/app.js"  app-js-response
        "/app.css" app-css-response
        {:status 404}))))

(defn serve [content]
  (hs/run-server (make-handler content) {:port 8888}))
