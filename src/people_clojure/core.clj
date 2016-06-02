(ns people-clojure.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])
  (:gen-class))

(defn break-line-apart [line] (str/split line #","))

(defn read-people []
  (let [
        people (slurp "people.csv")
        people (str/split-lines people)
        people (map break-line-apart people)
        headers (first people)
        people (rest people)
        people (map (fn [person]
                      (zipmap headers person)) people)]
    people))

(defn people-html [country]
  (let [
        people (read-people)
        people (if (= 0 (count country))
                  people
                 (filter (fn [person]
                           (= (get person "country") country))
                       people))]
     [:ol
      (map (fn [person]
             [:li (str (get person "first_name") " " (get person "last_name"))])
           people)]))

(c/defroutes app
             (c/GET "/:country{.*}" [country]
               (h/html [:html
                        [:body
                         [:a {:href "http://theironyard.com"} "The iron yard"]
                         [:br]
                         (people-html country)]])))


(defonce server (atom nil))

(defn -main []
  (when @server (.stop @server))
  (reset! server (j/run-jetty app {:port 3000 :join? false})))
