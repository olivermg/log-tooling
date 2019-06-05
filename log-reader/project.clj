(defproject log-reader "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[http-kit "2.3.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/clojure "1.10.0"]
                 [tick "0.4.14-alpha"]
                 [zprint "0.4.15"]

                 ;;; to avoid version conflicts:
                 [org.clojure/tools.reader "1.2.2" :exclusions [org.clojure/clojure]]]
  :plugins [[io.taylorwood/lein-native-image "0.3.0"]]
  :main ^:skip-aot log-reader.core
  :target-path "target/%s"
  :native-image {;;;graal-bin ""  ;; set this via env var GRAALVM_HOME if not in PATH already
                 :name     "logreader"
                 :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                 :opts     ["--initialize-at-build-time"
                            "--no-fallback"
                            "--static"
                            "--verbose"]}
  :profiles {:uberjar {:aot :all}}
  :pedantic? :abort)
