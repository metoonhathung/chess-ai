// Setup basic express server
const express = require("express");
const app = express();
const path = require("path");
const server = require("http").createServer(app);
const io = require("socket.io")(server);
const port = process.env.PORT || 3000;

server.listen(port, () => {
  console.log("Server listening at port %d", port);
});

app.use(express.static(path.join(__dirname, "public")));

const rooms = {};

io.on("connection", (socket) => {
  console.log(`Socket ${socket.id} connected`);

  let roomName = null;

  socket.on("join", (name) => {
    roomName = name;
    if (!(name in rooms)) {
      rooms[name] = 0;
    }
    if (rooms[name] >= 2) {
      return;
    }
    rooms[name]++;
    console.log(`Socket ${socket.id} joined room ${name} (total: ${rooms[name]})`);
    socket.join(name);
    socket.emit("join", rooms[name]);
    socket.to(name).emit("join", rooms[name]);
  });

  socket.on("leave", (name) => {
    roomName = null;
    rooms[name]--;
    console.log(`Socket ${socket.id} left room ${name} (total: ${rooms[name]})`);
    socket.leave(name);
    socket.to(name).emit("leave", rooms[name]);
    if (rooms[name] === 0) {
      delete rooms[name];
    }
  });

  socket.on("make_move", (move) => {
    console.log(`Socket ${socket.id} made move ${move}`);
    socket.to(roomName).emit("make_move", move);
  });

  socket.on("unmake_move", () => {
    console.log(`Socket ${socket.id} unmade move`);
    socket.to(roomName).emit("unmake_move");
  });

  socket.on("draw", () => {
    console.log(`Socket ${socket.id} drew`);
    socket.to(roomName).emit("draw");
  });

  socket.on("resign", () => {
    console.log(`Socket ${socket.id} resigned`);
    socket.to(roomName).emit("resign");
  });

  socket.on("disconnect", () => {
    console.log(`Socket ${socket.id} disconnected`);
  });
});
