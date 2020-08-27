(ns billsplit-clojure.web
  (:require [hiccup.core :refer [html]]
            [hiccup.page :as page]))

(defn layout
  "Applies a layout to html pages."
  [page]
  (page/html5
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name    "viewport"
             :content "width=device-width, initial-scale=1.0"}]

     [:script {:src "https://code.jquery.com/jquery-latest.min.js"}]

     [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/icon?family=Material+Icons"}]
     [:link {:rel "stylesheet" :href "https://code.getmdl.io/1.3.0/material.indigo-pink.min.css"}]
     [:script {:defer "true" :src "https://code.getmdl.io/1.3.0/material.min.js"}]
     [:script {:src "//platform-api.sharethis.com/js/sharethis.js#property=5b80b0e9d09af600128a4bef&product=inline-share-buttons"}]

    (page/include-css "css/styles.css")
    (page/include-js "js/scripts.js")]

    [:body
     [:div.mdl-layout.mdl-js-layout.mdl-layout--fixed-header
      [:header.mdl-layout__header
       [:div.mdl-layout__header-row
        [:span.mdl-layout-title "Billsplit"]
        [:div.mdl-layout-spacer]
        [:div.mdl-textfield.mdl-js-textfield.getmdl-select {:style "width:100px"}]
        [:nav.mdl-navigation.mdl-layout--large-screen-only]]]
      [:div.mdl-layout__drawer
       [:span.mdl-layout-title "Billsplit"]
       [:nav.mdl-navigation
        [:a.mdl-navigation__link {:href "/"} "Split a bill"]
        [:a.mdl-navigation__link {:href "/about"} "About"]]]
      [:main.mdl-layout__content
       [:div.page-content page]]]
     ]))

;(defn index []
;  (page/html5
;    [:head
;     [:title "Billsplit"]]
;    [:body
;     [:div {:id "content"} "Welcome to Billsplit!"]]))


(defn index []
  (page/html5
    [:head
     [:title "Billsplit"]]
     [:body
      [:form#form {:autocomplete "off" :role "form" :action "/split" :method "post"}
      [:div.row
       [:div#fields.control-group
        [:div.controls
          [:div.mdl-cell.mdl-cell--12-col
           [:h5 "Welcome to Billsplit!"]
           [:h6 "Please insert the name of the people who are splitting the bill below:"]]
          [:div.mdl-cell.mdl-cell--8-col
           [:div.form-group
            [:div.mdl-textfield.mdl-js-textfield
             [:input#p1.form-control.mdl-textfield__input {:placeholder "e.g. John" :name "fields[]" :type "text" :autofocus "true"}]]]]
          [:div.mdl-cell.mdl-cell--4-col
           [:button.mdl-button.mdl-js-button.mdl-button--fab.mdl-button--mini-fab.btn-add {:type "button"}
            [:i.material-icons "add"]]
           [:button.mdl-button.mdl-js-button.mdl-button--fab.mdl-button--mini-fab.btn-remove {:type "button"}
            [:i.material-icons "delete"]]]]]]
       [:div.mdl-cell.mdl-cell--1-col
        [:button#submitbutton.mdl-button.mdl-js-button.mdl-button--raised.submitbutton {:type "submit"} "Continue"]]]]))


; next step: translate split.html to hiccup split
; reference for loops: https://github.com/weavejester/hiccup
(defn split [people-list]
  (page/html5
    [:head [:title "Billsplit"]]
    [:body
     "Oi"
      ]
     ))