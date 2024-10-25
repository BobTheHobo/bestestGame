package com.mygame;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.math.FastMath;

/*
* author: shawn
*/
public class CircleMesh extends Mesh {

    public CircleMesh(int samples, float radius) {
        // Number of vertices: center + samples around the circle
        int vertexCount = samples + 1;

        // Positions array
        float[] positions = new float[vertexCount * 3]; // x, y, z for each vertex

        // Indices array
        int[] indices = new int[samples * 3]; // 3 indices per triangle

        // Center vertex at (0,0,0)
        positions[0] = 0;
        positions[1] = 0;
        positions[2] = 0;

        // Generate vertices around the circle
        for (int i = 0; i < samples; i++) {
            float angle = FastMath.TWO_PI * i / samples;
            float x = FastMath.cos(angle) * radius;
            float y = FastMath.sin(angle) * radius;

            positions[(i + 1) * 3] = x;
            positions[(i + 1) * 3 + 1] = y;
            positions[(i + 1) * 3 + 2] = 0;

            // Triangle indices
            indices[i * 3] = 0; // center vertex
            indices[i * 3 + 1] = i + 1;
            indices[i * 3 + 2] = (i + 2 > samples) ? 1 : i + 2;
        }

        // Set buffers
        setBuffer(VertexBuffer.Type.Position, 3, positions);
        setBuffer(VertexBuffer.Type.Index, 3, indices);

        // Update the mesh
        updateBound();
    }
}
