/*
 * Copyright (c) 2016, the JUNG Project and the Regents of the University 
 * of California.  All rights reserved.
 *
 * This software is open-source under the BSD license; see
 * https://github.com/jrtom/jung/blob/master/LICENSE for a description.
 */
package edu.uci.ics.jung.algorithms.generators.random;

import com.google.common.base.Supplier;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;

import junit.framework.TestCase;

/**
 * @author W. Giordano
 * @author Scott White
 * @author James Marchant
 */
public class TestBarabasiAlbert extends TestCase {
	protected Supplier<Integer> vertexFactory;
	protected Supplier<Number> edgeFactory;

	protected int init_vertices = 1;
	protected int edges_to_add_per_timestep = 1;
	protected int random_seed = 0;
	protected int num_timesteps = 10;
	protected int num_tests = 10;

	@Override
	protected void setUp() {
		vertexFactory = new Supplier<Integer>() {
			int count;

			public Integer get() {
				return count++;
			}
		};
		edgeFactory = new Supplier<Number>() {
			int count;

			public Number get() {
				return count++;
			}
		};
	}

	// TODO(jrtom): add tests for
	// * parallel edges
	// * undirected edges
	// * ...
	public void testDirected() 
    {
        int init_vertices = 1;
        int edges_to_add_per_timestep = 1;
        int random_seed = 0;
        int num_tests = 10;
        int num_timesteps = 10;
        
    	Supplier<Integer> vertexFactory = 
    		new Supplier<Integer>() {
    			int count;
				public Integer get() {
					return count++;
				}};
		Supplier<Number> edgeFactory = 
		    new Supplier<Number>() {
			    int count;
				public Number get() {
					return count++;
				}};

	    BarabasiAlbertGenerator<Integer,Number> generator = 
            new BarabasiAlbertGenerator<>(NetworkBuilder.directed(), vertexFactory, edgeFactory,
            		init_vertices, edges_to_add_per_timestep, random_seed);
	    for (int i = 1; i <= num_tests; i++) {
	        generator.evolveGraph(num_timesteps);
	        Network<Integer, Number> graph = generator.get();
	        assertEquals(graph.nodes().size(), (i*num_timesteps) + init_vertices);
	        assertEquals(graph.edges().size(), edges_to_add_per_timestep * (i*num_timesteps));
	        
			for (Integer v : graph.nodes()) {
		        // Every node should have an out-degree AT LEAST equal to the number of
		        // edges added per timestep (dependent on if it is directed or undirected).
				assertTrue(graph.outDegree(v) >= edges_to_add_per_timestep);
				
				// Check that not every edge goes to one node; the in-degree of any node
				// should be strictly less than the number of edges.
				assertTrue(graph.inDegree(v) < graph.edges().size());
			}
	    }
    }

	@SuppressWarnings("unused")
	public void testPreconditions() {
		try {
			BarabasiAlbertGenerator<Integer,Number> generator = 
	            new BarabasiAlbertGenerator<>(NetworkBuilder.directed(),
	            	vertexFactory,
	            	edgeFactory,
	            	0, // init_vertices
	            	edges_to_add_per_timestep,
	            	random_seed);
			fail("failed to reject init_vertices of <= 0");
		} catch (IllegalArgumentException e) {
			// TODO: assert that the exception message contains "seed"
		}

		// test edges_to_add_per_timestep = 0
		try {
			BarabasiAlbertGenerator<Integer,Number> generator = 
	            new BarabasiAlbertGenerator<>(NetworkBuilder.directed(),
	            	vertexFactory,
	            	edgeFactory,
	            	init_vertices,
	            	0, // edges_to_add_per_timestep
	            	random_seed);
			fail("failed to reject edges_to_add_per_timestamp of <= 0");
		} catch (IllegalArgumentException e) {
			// TODO: assert that the exception message is approx:
			// "Number of edges to attach at each time step must be positive"
		}

		// test edges_to_add_per_timestep > init_vertices
		try {
			int nodesToAdd = 5;
		    BarabasiAlbertGenerator<Integer,Number> generator = 
	            new BarabasiAlbertGenerator<>(NetworkBuilder.directed(),
	            	vertexFactory,
	            	edgeFactory,
	            	nodesToAdd,  // init_vertices
	            	nodesToAdd + 1, // edges_to_add_per_timestep
	            	random_seed);
			fail("failed to reject edges_to_add_per_timestamp of > init_vertices");
		} catch (IllegalArgumentException e) {
			// TODO: assert that the exception message is appropriate (see above)
		}
	}

}
