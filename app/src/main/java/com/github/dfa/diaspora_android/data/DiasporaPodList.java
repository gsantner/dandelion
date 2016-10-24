package com.github.dfa.diaspora_android.data;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Created by gsantner (https://gsantner.github.io/ on 30.09.16.
 * DiasporaPodList  - List container for DiasporaPod's, with methods to merge with other DiasporaPodLists
 * DiasporaPod      - Data container for a Pod, can include N DiasporaPodUrl's
 * DiasporaPodUrl   - A Url of an DiasporaPod
 * For all Classes a loading and saving to JSON method is available
 */
public class DiasporaPodList implements Iterable<DiasporaPodList.DiasporaPod>, Serializable {
    private List<DiasporaPod> pods = new ArrayList<>();
    private boolean trackMergeChanges = false;
    private Integer trackAddedIndexStart = -1;
    private List<Integer> trackUpdatedIndexes = new ArrayList<>();
    private boolean keepOldNameDuringMerge = false;
    private long timestamp;

    public DiasporaPodList() {
    }

    /**
     * Load DiasporaPodList from Json
     *
     * @param json Json Object
     */
    public DiasporaPodList fromJson(JSONObject json) throws JSONException {
        JSONArray jarr;
        pods.clear();

        if (json.has("pods")) {
            jarr = json.getJSONArray("pods");
            for (int i = 0; i < jarr.length(); i++) {
                DiasporaPod pod = new DiasporaPod().fromJson(jarr.getJSONObject(i));
                pods.add(pod);
            }
        }
        if (json.has("timestamp")) {
            timestamp = json.getLong("timestamp");
        }
        return this;
    }

    /**
     * Convert DiasporaPodList to JSON
     */
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray jpods = new JSONArray();
        for (DiasporaPod pod : pods) {
            jpods.put(pod.toJson());
        }
        json.put("pods", jpods);
        json.put("timestamp", System.currentTimeMillis());
        return json;
    }

    /**
     * Merge newer entries into this podlist
     * Will add new pods, and update data of pods with data from the new list
     *
     * @param newPodList Another podlist
     */
    public void mergeWithNewerEntries(final DiasporaPodList newPodList) throws JSONException {
        if (isTrackMergeChanges()) {
            trackAddedIndexStart = -1;
            trackUpdatedIndexes.clear();
        }
        for (DiasporaPod newPod : newPodList) {
            int index = pods.indexOf(newPod);
            if (index >= 0) {
                DiasporaPod updatePodBak = new DiasporaPod().fromJson(pods.get(index).toJson());
                DiasporaPod updatePod = pods.get(index);
                updatePod.fromJson(newPod.toJson());

                // Restore Pod id (if was set to zero)
                if (updatePodBak.getId() != 0 && updatePod.getId() == 0) {
                    updatePod.setId(updatePodBak.getId());
                }
                if (updatePodBak.getActive6() != 0 && updatePod.getActive6() == 0) {
                    updatePod.setActive6(updatePodBak.getActive6());
                }
                if (updatePodBak.getScore() != 0 && updatePod.getScore() == 0) {
                    updatePod.setScore(updatePodBak.getScore());
                }
                if (!updatePodBak.getName().equals("") && keepOldNameDuringMerge) {
                    updatePod.setName(updatePodBak.getName());
                }
                if (isTrackMergeChanges()) {
                    trackUpdatedIndexes.add(index);
                }
            } else {
                pods.add(newPod);
                if (isTrackMergeChanges() && trackAddedIndexStart == -1) {
                    trackAddedIndexStart = pods.size() - 1;
                }
            }
        }
    }

    /**
     * Sort the pod list
     */
    public void sortPods() {
        Collections.sort(pods);
    }

    /**
     * Iterator for Iterable interface (forEach, ..)
     */
    public Iterator<DiasporaPod> iterator() {
        return pods.iterator();
    }

    public int size() {
        return pods.size();
    }

    public int indexOf(DiasporaPod pod) {
        return pods.indexOf(pod);
    }

    public List<DiasporaPod> getPods() {
        return pods;
    }

    public void setPods(List<DiasporaPod> pods) {
        this.pods = pods;
    }

    public DiasporaPod getPodAt(int index) {
        if (index >= 0 && index < pods.size()) {
            return pods.get(index);
        }
        return null;
    }

    public boolean isTrackMergeChanges() {
        return trackMergeChanges;
    }

    public void setTrackMergeChanges(boolean trackMergeChanges) {
        this.trackMergeChanges = trackMergeChanges;
    }

    public Integer getTrackAddedIndexStart() {
        return trackAddedIndexStart;
    }

    public List<Integer> getTrackUpdatedIndexes() {
        return trackUpdatedIndexes;
    }

    public boolean isKeepOldNameDuringMerge() {
        return keepOldNameDuringMerge;
    }

    public void setKeepOldNameDuringMerge(boolean keepOldNameDuringMerge) {
        this.keepOldNameDuringMerge = keepOldNameDuringMerge;
    }


    /*      ██████╗  ██████╗ ██████╗
     *      ██╔══██╗██╔═══██╗██╔══██╗
     *      ██████╔╝██║   ██║██║  ██║
     *      ██╔═══╝ ██║   ██║██║  ██║
     *      ██║     ╚██████╔╝██████╔╝
     *      ╚═╝      ╚═════╝ ╚═════╝
     */
    public static class DiasporaPod implements Iterable<DiasporaPodList.DiasporaPod.DiasporaPodUrl>, Comparable<DiasporaPod>, Serializable {
        private List<DiasporaPodUrl> podUrls = new ArrayList<>();
        private List<String> mainLangs = new ArrayList<>();
        private String name = "";
        private int score = 0;
        private int id = 0;
        private long active6 = 0;


        public DiasporaPod() {
        }

        /**
         * Load a DiasporaPod from JSON
         *
         * @param json Json Object
         */
        public DiasporaPod fromJson(JSONObject json) throws JSONException {
            JSONArray jarr;

            if (json.has("name")) {
                name = json.getString("name");
            }
            if (json.has("mainLangs")) {
                jarr = json.getJSONArray("mainLangs");
                for (int i = 0; i < jarr.length(); i++) {
                    String val = jarr.getString(i);
                    if (!mainLangs.contains(val)) {
                        mainLangs.add(val);
                    }
                }
            }
            if (json.has("podUrls")) {
                jarr = json.getJSONArray("podUrls");
                for (int i = 0; i < jarr.length(); i++) {
                    DiasporaPodUrl podUrl = new DiasporaPodUrl().fromJson(jarr.getJSONObject(i));
                    if (!podUrls.contains(podUrl)) {
                        podUrls.add(podUrl);
                    }
                }
            }
            if (json.has("score")) {
                score = json.getInt("score");
            }
            if (json.has("active6")) {
                active6 = json.getLong("active6");
            }
            if (json.has("id")) {
                id = json.getInt("id");
            }
            return this;
        }

        /**
         * Convert DiasporaPod to JSON
         */
        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("score", score);
            json.put("active6", active6);
            json.put("id", id);

            // Pod urls
            JSONArray jarr = new JSONArray();
            for (DiasporaPodUrl value : podUrls) {
                jarr.put(value.toJson());
            }
            json.put("podUrls", jarr);

            // main langs
            jarr = new JSONArray();
            for (String value : mainLangs) {
                jarr.put(value);
            }
            json.put("mainLangs", jarr);
            return json;
        }

        @Override
        public boolean equals(Object o) {
            boolean ret = false;
            if (o instanceof DiasporaPod) {
                DiasporaPod otherPod = (DiasporaPod) o;

                // Check if id is equal
                ret = this.id != 0 && this.id == otherPod.id;

                // Check if host is the same (fallback if id is 0)
                if (!ret) {
                    for (DiasporaPodUrl podUrl : podUrls) {
                        for (DiasporaPodUrl otherPodUrl : otherPod.getPodUrls()) {
                            if (podUrl.getBaseUrl().equals(otherPodUrl.getBaseUrl())) {
                                ret = true;
                            }
                        }
                    }
                }
            }
            return ret;
        }

        @Override
        public int compareTo(@NonNull DiasporaPod otherPod) {
            List<DiasporaPodUrl> myPodUrls = getPodUrls();
            List<DiasporaPodUrl> otherPodUrls = otherPod.getPodUrls();
            if (!myPodUrls.isEmpty() && !otherPodUrls.isEmpty()) {
                return myPodUrls.get(0).getHost().compareTo(otherPodUrls.get(0).getHost());
            }
            return name.compareTo(otherPod.getName());
        }

        @Override
        public String toString() {
            return name + "(" + id + ")";
        }

        /**
         * Iterator for Iterable interface (forEach, ..)
         */
        public Iterator<DiasporaPodUrl> iterator() {
            return podUrls.iterator();
        }

        /*
        * Getter & Setter
        */
        public List<DiasporaPodUrl> getPodUrls() {
            return podUrls;
        }

        public DiasporaPod setPodUrls(List<DiasporaPodUrl> podUrls) {
            this.podUrls = podUrls;
            return this;
        }

        public List<String> getMainLangs() {
            return mainLangs;
        }

        public DiasporaPod setMainLangs(List<String> mainLangs) {
            this.mainLangs = mainLangs;
            return this;
        }

        public DiasporaPod appendMainLangs(String... values) {
            for (String mainLang : values) {
                this.mainLangs.add(mainLang);
            }
            return this;
        }

        /**
         * Returns the first DiasporaPodUrl in the list
         */
        public DiasporaPodUrl getPodUrl() {
            if (podUrls.size() > 0) {
                return podUrls.get(0);
            }
            return null;
        }

        public DiasporaPod appendPodUrls(DiasporaPodUrl... values) {
            for (DiasporaPodUrl value : values) {
                this.podUrls.add(value);
            }
            return this;
        }

        public String getName() {
            return name;
        }

        public DiasporaPod setName(String name) {
            this.name = name;
            return this;
        }

        public int getScore() {
            return score;
        }

        public DiasporaPod setScore(int score) {
            this.score = score;
            return this;
        }

        public long getActive6() {
            return active6;
        }

        public DiasporaPod setActive6(long active6) {
            this.active6 = active6;
            return this;
        }

        public int getId() {
            return id;
        }

        public DiasporaPod setId(int id) {
            this.id = id;
            return this;
        }

        /*      ██████╗  ██████╗ ██████╗     ██╗   ██╗██████╗ ██╗
         *      ██╔══██╗██╔═══██╗██╔══██╗    ██║   ██║██╔══██╗██║
         *      ██████╔╝██║   ██║██║  ██║    ██║   ██║██████╔╝██║
         *      ██╔═══╝ ██║   ██║██║  ██║    ██║   ██║██╔══██╗██║
         *      ██║     ╚██████╔╝██████╔╝    ╚██████╔╝██║  ██║███████╗
         *      ╚═╝      ╚═════╝ ╚═════╝      ╚═════╝ ╚═╝  ╚═╝╚══════╝
         */
        public static class DiasporaPodUrl implements Serializable {
            private String host = "";
            private String protocol = "https";
            private Integer port = 443;

            public DiasporaPodUrl() {
            }

            public DiasporaPodUrl(JSONObject json) throws JSONException {
                fromJson(json);
            }

            /**
             * Get the base url
             *
             * @return baseUrl
             */
            public String getBaseUrl() {
                return protocol + "://" + host + (isPortNeeded() ? port : "");
            }

            /**
             * Convert JSON to DiasporaPodList
             *
             * @param json JSON Object
             */
            public DiasporaPodUrl fromJson(JSONObject json) throws JSONException {
                if (json.has("host")) {
                    host = json.getString("host");
                }
                if (json.has("protocol")) {
                    protocol = json.getString("protocol");
                }
                if (json.has("port")) {
                    port = json.getInt("port");
                }
                return this;
            }

            /***
             * Convert DiasporaPodList to JSON
             */
            public JSONObject toJson() throws JSONException {
                JSONObject json = new JSONObject();
                json.put("host", host);
                if (!protocol.equals("https")) {
                    json.put("protocol", protocol);
                }
                if (port != 443) {
                    json.put("port", port);
                }
                return json;
            }

            /**
             * Set default values for https
             */
            public void setHttpsDefaults() {
                setProtocol("https");
                setPort(443);
            }


            /**
             * Set default values for http
             */
            public void setHttpDefaults() {
                setProtocol("http");
                setPort(80);
            }

            /**
             * Tells if the ports needs to shown
             */
            public boolean isPortNeeded() {
                return !((port == 80 && protocol.equals("http")) || (port == 443 && protocol.equals("https")));
            }

            @Override
            public String toString() {
                return getBaseUrl();
            }

            @Override
            public boolean equals(Object o) {
                if (o instanceof DiasporaPodUrl) {
                    return getBaseUrl().equals(((DiasporaPodUrl) o).getBaseUrl());
                }
                return false;
            }

            /*
             *  GETTER & SETTER
             */
            public String getHost() {
                return host;
            }

            public DiasporaPodUrl setHost(String host) {
                this.host = host;
                return this;
            }

            public String getProtocol() {
                return protocol;
            }

            public DiasporaPodUrl setProtocol(String protocol) {
                this.protocol = protocol;
                return this;
            }

            public Integer getPort() {
                return port;
            }

            public DiasporaPodUrl setPort(Integer port) {
                this.port = port;
                return this;
            }
        }
    }
}
