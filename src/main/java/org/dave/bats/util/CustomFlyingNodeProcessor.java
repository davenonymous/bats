package org.dave.bats.util;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.SwimNodeProcessor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * This code has been copied from
 * https://github.com/ata4/dragon-mounts/blob/40bd58cfff2106e8c4b6a6ec43d5ea89ab5d7696/src/main/java/info/ata4/minecraft/dragon/server/entity/ai/path/NodeProcessorFlying.java
 *
 * The vanilla FlyingNodeProcessor can fly through walls diagonally.
 *
 */
public class CustomFlyingNodeProcessor extends SwimNodeProcessor {
    /**
     * Returns PathPoint for given coordinates
     */
    @Override
    public PathPoint getPathPointToCoords(double x, double y, double z) {
        return openPoint(
                MathHelper.floor(x - (entity.width / 2.0f)),
                MathHelper.floor(y - (entity.height / 2.0f)),
                MathHelper.floor(z - (entity.width / 2.0f))
        );
    }

    @Override
    public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
        BlockPos current = new BlockPos(currentPoint.x, currentPoint.y, currentPoint.z);

        boolean[][][] blocked = new boolean[3][3][3];
        for (EnumFacing.Axis axis : EnumFacing.Axis.values()) {
            for (EnumFacing.AxisDirection direction : EnumFacing.AxisDirection.values()) {
                int offset = direction.getOffset()+1;
                EnumFacing facing = EnumFacing.getFacingFromAxis(direction, axis);
                if(!blockaccess.isAirBlock(current.offset(facing))) {
                    for (int a = 0; a < 3; a++) {
                        for (int b = 0; b < 3; b++) {
                            if(axis == EnumFacing.Axis.X)blocked[offset][a][b] = true;
                            if(axis == EnumFacing.Axis.Y)blocked[a][offset][b] = true;
                            if(axis == EnumFacing.Axis.Z)blocked[a][b][offset] = true;
                        }
                    }
                }
            }
        }

        int i = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    if(blocked[x][y][z]) {
                        continue;
                    }

                    if(x == 1 && y == 1 && z == 1) {
                        continue;
                    }

                    PathPoint point = getSafePoint(entity, currentPoint.x + x-1, currentPoint.y + y-1, currentPoint.z + z-1);
                    if (point != null && !point.visited) {
                        pathOptions[i++] = point;
                    }
                }
            }

        }

        /*
        for (EnumFacing facing : EnumFacing.values()) {
            PathPoint point = getSafePoint(entity,
                    currentPoint.x + facing.getXOffset(),
                    currentPoint.y + facing.getYOffset(),
                    currentPoint.z + facing.getZOffset()
            );

            if (point != null && !point.visited && point.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = point;
            }
        }
        */

        return i;
    }

    /**
     * Returns a point that the entity can safely move to
     */
    private PathPoint getSafePoint(Entity entityIn, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);

        entitySizeX = MathHelper.floor(entityIn.width + 1);
        entitySizeY = MathHelper.floor(entityIn.height + 1);
        entitySizeZ = MathHelper.floor(entityIn.width + 1);

        for (int ix = 0; ix < entitySizeX; ++ix) {
            for (int iy = 0; iy < entitySizeY; ++iy) {
                for (int iz = 0; iz < entitySizeZ; ++iz) {
                    if(!blockaccess.isAirBlock(pos.add(ix, iy, iz))) {
                        return null;
                    }
                }
            }
        }

        return openPoint(x, y, z);
    }
}
