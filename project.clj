(defproject billsplit-clojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.stuartsierra/component "1.0.0"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [http-kit "2.3.0"]
                 [ring/ring-defaults "0.3.2"]
                 [prismatic/schema "1.1.12"]
                 [stasis "1.0.0"]]
  :repl-options {:init-ns billsplit-clojure.core}
  :main billsplit-clojure.core)
