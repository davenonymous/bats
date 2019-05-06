package org.dave.bats.util;

import net.minecraft.entity.passive.EntityBat;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.mini2Dx.gdx.math.CatmullRomSpline;
import org.mini2Dx.gdx.math.Vector3;

import javax.annotation.Nullable;

public class PathHelper {
    NodeProcessor processor;
    PathFinder pathFinder;

    public PathHelper() {
        this.processor = new CustomFlyingNodeProcessor();
        this.pathFinder = new PathFinder(this.processor);
    }

    @Nullable
    public Path findPath(World world, BlockPos start, BlockPos end, float maxDistance) {
        Logz.info("Looking for path from %s to %s", start, end);
        EntityBat bat = new EntityBat(world);
        bat.posX = start.getX() + 0.5d;
        bat.posY = start.getY() + 0.5d;
        bat.posZ = start.getZ() + 0.5d;
        //bat.setEntityBoundingBox(new AxisAlignedBB(bat.posX - bat.width/2, bat.posY - 0.5d, bat.posZ - bat.width/2, bat.posX + bat.width/2, bat.posY + 0.05d, bat.posZ + bat.width/2));
        bat.setEntityBoundingBox(new AxisAlignedBB(bat.posX - 0.005d, bat.posY - 0.005d, bat.posZ - 0.005d, bat.posX + 0.005d, bat.posY + 0.005d, bat.posZ + 0.005d));

        Path result = this.pathFinder.findPath(world, bat, end, maxDistance);



        world.removeEntity(bat);

        return result;
    }

    public static Path extendPathByStartAndEnd(Path path, BlockPos start, BlockPos end) {
        PathPoint[] extended = new PathPoint[path.getCurrentPathLength()+1];
        //extended[0] = new PathPoint(start.getX(), start.getY(), start.getZ());
        for (int index = 0; index < path.getCurrentPathLength(); index++) {
            extended[index] = path.getPathPointFromIndex(index);
        }
        extended[extended.length-1] = new PathPoint(end.getX(), end.getY(), end.getZ());

        return new Path(extended);
    }

    public static CatmullRomSpline<Vector3> catmull(Path path) {
        if(path == null) {
            return null;
        }

        Vector3 controlPoints[] = new Vector3[path.getCurrentPathLength()+2];
        PathPoint pointB = path.getPathPointFromIndex(0);
        controlPoints[0] = new Vector3(pointB.x, pointB.y, pointB.z);

        for (int index = 0; index < path.getCurrentPathLength(); index++) {
            PathPoint point = path.getPathPointFromIndex(index);
            controlPoints[index+1] = new Vector3(point.x, point.y, point.z);
        }

        PathPoint pointC = path.getPathPointFromIndex(path.getCurrentPathLength()-1);
        controlPoints[controlPoints.length-1] = new Vector3(pointC.x, pointC.y, pointC.z);

        return new CatmullRomSpline<>(controlPoints, false);
    }

    public static boolean stillValid(World world, Path path) {
        for (int index = 1; index < path.getCurrentPathLength()-1; index++) {
            PathPoint point = path.getPathPointFromIndex(index);
            BlockPos pos = new BlockPos(point.x, point.y, point.z);
            if(!world.isAirBlock(pos)) {
                Logz.info("Path invalid id=%d, pos=%s --> %s", index, pos, world.getBlockState(pos));
                return false;
            }
        }

        return true;
    }

}
