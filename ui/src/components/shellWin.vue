<template>
  <div id="terminal" class="xterm"></div>
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
      // const connectInfo = {
      //   host: "175.27.190.52",
      //   port: "22",
      //   user: "root",
      //   password: "Admin123",
      // };
      
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