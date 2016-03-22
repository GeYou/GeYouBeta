package com.thesis.geyoubeta.parser;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import static java.lang.Integer.compare;

/**
 * Created by jethrodivino on 22/03/2016.
 */
public class DirectionJSONDijkstra {
    public List<LatLng> parse(JSONObject jObject) {

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        LatLng startLocation = null,endLocation = null;
        List <LatLng> URL = null;
        try {
            jRoutes = jObject.getJSONArray("routes");
            List<Graph.Edge> path = new ArrayList<Graph.Edge>();
//            LatLng startLocation = new LatLng(Double.parseDouble((String)((JSONObject)((JSONObject)((JSONObject)jRoutes.get(0)).get("bounds")).get("northeast")).get("lat")),Double.parseDouble((String)((JSONObject)((JSONObject)((JSONObject)jRoutes.get(0)).get("bounds")).get("northeast")).get("long")));
//            LatLng endLocation = new LatLng(Double.parseDouble((String)((JSONObject)((JSONObject)((JSONObject)jRoutes.get(0)).get("bounds")).get("southwest")).get("lat")),Double.parseDouble((String)((JSONObject)((JSONObject)((JSONObject)jRoutes.get(0)).get("bounds")).get("northeast")).get("long")));
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                Log.d("watda: ", "routedijkstra");
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                startLocation = new LatLng((Double) ((JSONObject) ((JSONObject) jLegs.get(0)).get("start_location")).get("lat"), (Double) ((JSONObject) ((JSONObject) jLegs.get(0)).get("start_location")).get("lng"));
                endLocation = new LatLng((Double) ((JSONObject) ((JSONObject) jLegs.get(0)).get("end_location")).get("lat"), (Double)((JSONObject)((JSONObject)jLegs.get(0)).get("end_location")).get("lng"));
                /** Traversing all legs */
                Log.d("watda",startLocation.toString());
                Log.d("watda",endLocation.toString());
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++){
                        LatLng a = new LatLng((double) (Double) ((JSONObject) ((JSONObject) jSteps.get(k)).get("start_location")).get("lat"), (double) (Double) ((JSONObject) ((JSONObject) jSteps.get(k)).get("start_location")).get("lng"));
                        LatLng b = new LatLng((double) (Double) ((JSONObject) ((JSONObject) jSteps.get(k)).get("end_location")).get("lat"), (double) (Double) ((JSONObject) ((JSONObject) jSteps.get(k)).get("end_location")).get("lng"));
                        int distance = (Integer) ((JSONObject) ((JSONObject) jSteps.get(k)).get("distance")).get("value");
                        Log.d("A",a.toString());
                        path.add(new Graph.Edge(a,b,distance));
                    }
                }
            }
            Graph g = new Graph(path);
            g.dijkstra(startLocation);
            URL=g.printPath(endLocation);
            Log.d("URL", String.valueOf(URL.size()));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
//        Log.d("anaderwatda: ", String.valueOf(routes.size()));
        return URL;
    }


    static class Graph {
        private final Map<LatLng, Vertex> graph; // mapping of vertex names to Vertex objects, built from a set of Edges

        /**
         * One edge of the graph (only used by Graph constructor)
         */
        public static class Edge {
            public final LatLng v1, v2;
            public final int dist;

            public Edge(LatLng v1, LatLng v2, int dist) {
                this.v1 = v1;
                this.v2 = v2;
                this.dist = dist;
                Log.d("AWE","EDGE");
            }
        }

        /**
         * One vertex of the graph, complete with mappings to neighbouring vertices
         */
        public static class Vertex implements Comparable<Vertex> {
            public final LatLng name;
            public int dist = Integer.MAX_VALUE; // MAX_VALUE assumed to be infinity
            public Vertex previous = null;
            public final Map<Vertex, Integer> neighbours = new HashMap<>();

            public Vertex(LatLng name) {
                this.name = name;
                Log.d("VERTEX",name.toString());
            }

            private List<LatLng> printPath() {
                List<LatLng> retVal = new ArrayList<>() ;

//                if (this == this.previous) {
//                    retVal = String.valueOf(this.name);
//                    Log.d("DIJKPATH",retVal);
//                } else if (this.previous == null) {
//                    Log.d("DIJKPATH","unreachable");
//                } else {
////                    retval = retval != null ? retval.concat("|" + this.previous.printPath()) : this.previous.printPath();
//                    Log.d("DIJKPATH",retVal);
//                    System.out.printf(" -> %s(%d)", this.name, this.dist);
//                }
                Vertex trav;
                for(trav = this.previous; trav!=trav.previous;trav=trav.previous) {
                    if (trav == trav.previous) {
                        retVal.add(trav.name);
                    }else {
                        retVal.add(trav.name);
                    }
                    Log.d("RETVAL", String.valueOf(retVal.size()));
                }
                return retVal;
            }

            public int compareTo(Vertex other) {
                return compare(dist, other.dist);
            }
        }

        /**
         * Builds a graph from a set of edges
         * @param edges
         */
        public Graph(List<Edge> edges) {
            graph = new HashMap<>(edges.size());

            //one pass to find all vertices
            for (Edge e : edges) {
                if (!graph.containsKey(e.v1)) graph.put(e.v1, new Vertex(e.v1));
                if (!graph.containsKey(e.v2)) graph.put(e.v2, new Vertex(e.v2));
            }

            //another pass to set neighbouring vertices
            for (Edge e : edges) {
                graph.get(e.v1).neighbours.put(graph.get(e.v2), e.dist);
                //graph.get(e.v2).neighbours.put(graph.get(e.v1), e.dist); // also do this for an undirected graph
            }
        }

        /**
         * Runs dijkstra using a specified source vertex
         */
        public void dijkstra(LatLng startName) {
            Log.d("DIJK",startName.toString());
            if (!graph.containsKey(startName)) {
                System.err.printf("Graph doesn't contain start vertex \"%s\"\n", startName);
                return;
            }
            final Vertex source = graph.get(startName);
            NavigableSet<Vertex> q = new TreeSet<>();

            // set-up vertices
            for (Vertex v : graph.values()) {
                v.previous = v == source ? source : null;
                v.dist = v == source ? 0 : Integer.MAX_VALUE;
                q.add(v);
            }

            dijkstra(q);
        }

        /**
         * Implementation of dijkstra's algorithm using a binary heap.
         */
        private void dijkstra(final NavigableSet<Vertex> q) {
            Vertex u, v;
            while (!q.isEmpty()) {
                Log.d("DIJK","FINAL");
                u = q.pollFirst(); // vertex with shortest distance (first iteration will return source)
                if (u.dist == Integer.MAX_VALUE)
                    break; // we can ignore u (and any other remaining vertices) since they are unreachable

                //look at distances to each neighbour
                for (Map.Entry<Vertex, Integer> a : u.neighbours.entrySet()) {
                    v = a.getKey(); //the neighbour in this iteration

                    final int alternateDist = u.dist + a.getValue();
                    if (alternateDist < v.dist) { // shorter path to neighbour found
                        q.remove(v);
                        v.dist = alternateDist;
                        v.previous = u;
                        q.add(v);
                    }
                }
            }
        }

        /**
         * Prints a path from the source to the specified vertex
         */
        public List<LatLng> printPath(LatLng endName) {
            List<LatLng> retVal;
            if (!graph.containsKey(endName)) {
                System.err.printf("Graph doesn't contain end vertex \"%s\"\n", endName);
                return null;
            }
            Log.d("PRINT END",endName.toString());
            retVal=graph.get(endName).printPath();
//            Log.d("RETVAL PRINT", "NULL RETVAL");
            Log.d("RETVAL PRINT", String.valueOf(retVal.size()));
            return retVal;
        }

        /**
         * Prints the path from the source to every vertex (output order is not guaranteed)
         */
    }
}
