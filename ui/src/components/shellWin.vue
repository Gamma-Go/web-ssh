<template>
  <div id="terminal" class="shellWin"></div>
</template>
<script>
import "xterm/css/xterm.css";
import { Terminal } from "xterm";
import { FitAddon } from "xterm-addon-fit";
import { AttachAddon } from "xterm-addon-attach";
export default {
  name: "shellWin",
  data() {
    return {
      socket: null,
      term: null,
    };
  },
  beforeUnmount() {
    this.socket.close();
    this.term.dispose();
  },
  mounted() {
    const encryptStr = this.$route.query.params;
    this.initWebsocket(encryptStr);
  },
  methods: {
    init() {
      this.term = new Terminal();
      const fitAddon = new FitAddon();
      const attachAddon = new AttachAddon(this.socket);
      this.term.loadAddon(attachAddon);
      this.term.loadAddon(fitAddon);
      this.term.open(document.getElementById("terminal"));
      fitAddon.fit();
    },
    initWebsocket(encryptStr) {
      this.socket = new WebSocket(
        `ws://localhost:8081/ws/ssh?connectParams=${encryptStr}`
      );
      this.socketOnClose();
      this.socketOnOpen();
      this.socketOnError();
    },
    socketOnOpen() {
      this.socket.onopen = () => {
        this.init();
      };
    },
    socketOnClose() {
      this.socket.onclose = () => {
        this.term.write("\n服务器链接已断开");
      };
    },
    socketOnError() {
      this.socket.onerror = () => {
        this.term.write("\n服务器链接失败");
      };
    },
  },
};
</script>
<style type="text/css">
html,
body {
  padding: 0;
  margin: 0;
  height: 100%;
  overflow: hidden;
}
.shellWin {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0px;
  bottom: 0px;
  overflow: hidden;
  word-break: break-all;
  background: rgb(2, 2, 2);
}
</style>