For the past few weekends (sorry other mods) I've been working on my own take of a logistics mod.

With only a few things missing I'm ready to put this out there for beta testing. It is not ready to be included in major mod packs!

You could summarize it as "Phantomface-esque Storage Drawers Controller for any inventory".

It's bat themed, but that really is just a theme and does not influence the actual behavior in any way.
In other words, the bats are just a visual representation of linked blocks.

**So, how does it actually work?**

- Capture bats, craft a bat cage and link it to up to 8 nearby block faces.
  There must be an open path between the cage and the linked blocks!
- It does not transport items on its own, you need to push/pull into the bat cage, e.g. with a hopper
- This means transport speed depends on your insertion/extraction method
- Even though there are bats flying, transportation is instantaneous
- The bat cage basically represents all the linked blocks (like a phantomface)
- You can use the GUI to interact with the inventories directly or
- set filter rules for what can be import/exported from the linked inventories
  by external blocks.
- If you are not able to place an item in a slot then that is because automation
  would not be able to either.

**What kind of bugs am I expecting?**

- Performance issues with many nested cages connected to large inventories
- All the issues that come with logistics and transportation mods
- client/server issues, sync problems, calling non-existing code
- The must be a dupe bug in there somewhere
- Filters (ore, nbt, meta, mod) might not be matching like they should
- Weird slot behavior in the GUI
- Minor Rendering issues
- It's not balanced and it does not require much effort
- Capturing bats is annoying

**What will I definitely implement before hitting 1.0.0?**

- Fluid and Forge Energy support
- More server performance improvements. Cache all the things.
- Hwyla/TOP integration

**What other major features/refactors have I been thinking about, but can't implement because of time constraints**:

- Bats require food, i.e. you need to provide them with fruits -> less OP
- Links are saved on captured bats instead of on the batcage -> more convenience
- Instead of requiring 8 bats to craft a bat cage, make the bat cage accept individual bats -> makes a lot more sense
- Render the bats actually carrying the last transferred stack, instead of just representing the link -> immersion
- Configurable dash board with configurable slot visibility/positions, i.e. build your own gui from multiple blocks -> convenience

I appreciate any feedback here or on the github issue tracker. I'd prefer bugs on github though :), thanks