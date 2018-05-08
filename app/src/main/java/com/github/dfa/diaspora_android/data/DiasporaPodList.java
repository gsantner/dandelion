package com.github.dfa.diaspora_android.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Created by gsantner (http://gsantner.net/ on 30.09.16.
 * DiasporaPodList  - List container for DiasporaPod's, with methods to merge with other DiasporaPodLists
 * DiasporaPod      - Data container for a Pod, can include N DiasporaPodUrl's
 * DiasporaPodUrl   - A Url of an DiasporaPod
 * For all Classes a loading and saving to JSON method is available
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue", "SpellCheckingInspection", "UnusedReturnValue", "JavaDoc", "FieldCanBeLocal"})
public class DiasporaPodList implements Iterable<DiasporaPodList.DiasporaPod>, Serializable {
    private static final boolean EXPORT_TOJSON_POST_COUNT_LOCAL = true;
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
                if (updatePodBak.getPostCountLocal() != 0 && updatePod.getPostCountLocal() == 0) {
                    updatePod.setPostCountLocal(updatePodBak.getPostCountLocal());
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
        private List<DiasporaPodUrl> _podUrls = new ArrayList<>();
        private List<String> _mainLangs = new ArrayList<>();
        private String _name = "";
        private int _score = 0;
        private int _id = 0;
        private long _postCountLocal = 0;


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
                _name = json.getString("name");
            }
            if (json.has("mainLangs")) {
                jarr = json.getJSONArray("mainLangs");
                for (int i = 0; i < jarr.length(); i++) {
                    String val = jarr.getString(i);
                    if (!_mainLangs.contains(val)) {
                        _mainLangs.add(val);
                    }
                }
            }
            if (json.has("podUrls")) {
                jarr = json.getJSONArray("podUrls");
                for (int i = 0; i < jarr.length(); i++) {
                    DiasporaPodUrl podUrl = new DiasporaPodUrl().fromJson(jarr.getJSONObject(i));
                    if (!_podUrls.contains(podUrl)) {
                        _podUrls.add(podUrl);
                    }
                }
            }
            if (json.has("score")) {
                _score = json.getInt("score");
            }
            if (json.has("postCountLocal")) {
                _postCountLocal = json.getLong("postCountLocal");
            }
            if (json.has("id")) {
                _id = json.getInt("id");
            }
            return this;
        }

        /**
         * Convert DiasporaPod to JSON
         */
        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("name", _name);
            json.put("id", _id);

            if (_score != 0) {
                json.put("score", _score);
            }

            // Only export active6 (frequently changing if told to do)
            if (EXPORT_TOJSON_POST_COUNT_LOCAL && _postCountLocal > 0) {
                json.put("postCountLocal", _postCountLocal);
            }

            // Pod urls
            JSONArray jarr = new JSONArray();
            for (DiasporaPodUrl value : _podUrls) {
                jarr.put(value.toJson());
            }
            json.put("podUrls", jarr);

            // main langs
            jarr = new JSONArray();
            for (String value : _mainLangs) {
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
                ret = _id != 0 && _id == otherPod._id;

                // Check if _host is the same (fallback if id is 0)
                if (!ret) {
                    for (DiasporaPodUrl podUrl : _podUrls) {
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
        public int compareTo(DiasporaPod otherPod) {
            if (otherPod != null) {
                List<DiasporaPodUrl> myPodUrls = getPodUrls();
                List<DiasporaPodUrl> otherPodUrls = otherPod.getPodUrls();
                if (!myPodUrls.isEmpty() && !otherPodUrls.isEmpty()) {
                    return myPodUrls.get(0).getHost().compareTo(otherPodUrls.get(0).getHost());
                }
                return _name.compareTo(otherPod.getName());
            }
            return _name.compareTo("");
        }

        @Override
        public String toString() {
            return _name + "(" + _id + ")";
        }

        /**
         * Iterator for Iterable interface (forEach, ..)
         */
        public Iterator<DiasporaPodUrl> iterator() {
            return _podUrls.iterator();
        }

        /*
         * Getter & Setter
         */
        public List<DiasporaPodUrl> getPodUrls() {
            return _podUrls;
        }

        public DiasporaPod setPodUrls(List<DiasporaPodUrl> podUrls) {
            _podUrls = podUrls;
            return this;
        }

        public List<String> getMainLangs() {
            return _mainLangs;
        }

        public DiasporaPod setMainLangs(List<String> mainLangs) {
            _mainLangs = mainLangs;
            return this;
        }

        public DiasporaPod appendMainLangs(String... values) {
            _mainLangs.addAll(Arrays.asList(values));
            return this;
        }

        /**
         * Returns the first DiasporaPodUrl in the list
         */
        public DiasporaPodUrl getPodUrl() {
            if (_podUrls.size() > 0) {
                return _podUrls.get(0);
            }
            return null;
        }

        public DiasporaPod appendPodUrls(DiasporaPodUrl... values) {
            _podUrls.addAll(Arrays.asList(values));
            return this;
        }

        public String getName() {
            return _name;
        }

        public DiasporaPod setName(String name) {
            _name = name;
            return this;
        }

        public int getScore() {
            return _score;
        }

        public DiasporaPod setScore(int score) {
            _score = score;
            return this;
        }

        public long getPostCountLocal() {
            return _postCountLocal;
        }

        public DiasporaPod setPostCountLocal(long postCountLocal) {
            _postCountLocal = postCountLocal;
            return this;
        }

        public int getId() {
            return _id;
        }

        public DiasporaPod setId(int id) {
            _id = id;
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
            private String _host = "";
            private String _protocol = "https";
            private Integer _port = 443;

            public DiasporaPodUrl() {
            }

            public DiasporaPodUrl(JSONObject json) throws JSONException {
                fromJson(json);
            }

            /**
             * Get the base url
             *
             * @return
             */
            public String getBaseUrl() {
                return _protocol + "://" + _host + (isPortNeeded() ? _port : "");
            }

            /**
             * Convert JSON to DiasporaPodList
             *
             * @param json JSON Object
             */
            public DiasporaPodUrl fromJson(JSONObject json) throws JSONException {
                if (json.has("host")) {
                    _host = json.getString("host");
                }
                if (json.has("protocol")) {
                    _protocol = json.getString("protocol");
                }
                if (json.has("port")) {
                    _port = json.getInt("port");
                }
                return this;
            }

            /***
             * Convert DiasporaPodList to JSON
             */
            public JSONObject toJson() throws JSONException {
                JSONObject json = new JSONObject();
                json.put("host", _host);
                if (!_protocol.equals("https")) {
                    json.put("protocol", _protocol);
                }
                if (_port != 443) {
                    json.put("port", _port);
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
                return !((_port == 80 && _protocol.equals("http")) || (_port == 443 && _protocol.equals("https")));
            }

            @Override
            public String toString() {
                return getBaseUrl();
            }

            @Override
            @SuppressWarnings("SimplifiableIfStatement")
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
                return _host;
            }

            public DiasporaPodUrl setHost(String host) {
                _host = host;
                return this;
            }

            public String getProtocol() {
                return _protocol;
            }

            public DiasporaPodUrl setProtocol(String protocol) {
                _protocol = protocol;
                return this;
            }

            public Integer getPort() {
                return _port;
            }

            public DiasporaPodUrl setPort(Integer port) {
                _port = port;
                return this;
            }
        }
    }
}
