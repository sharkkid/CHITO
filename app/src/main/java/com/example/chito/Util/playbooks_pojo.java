package com.example.chito.Util;

import java.util.List;

public class playbooks_pojo {

    /**
     * backgroundSceneId : 1
     * scenes : [{"id":2,"initial":{"display":{"type":"webview","assetId":1}}},{"id":1,"initial":{"audio":{"tracks":[{"assetId":3,"instanceId":1,"fadeOutSeconds":5,"volume":{"type":"gps-distance-linear","latitude":25.044090901990533,"longtitude":121.51927395877044,"distanceVolumes":[[30,0.3],[5,0.9]]}},{"assetId":4,"instanceId":1,"fadeOutSeconds":5,"volume":{"type":"gps-distance-linear","latitude":25.043277672188704,"longtitude":121.51934933623511,"distanceVolumes":[[30,0.3],[5,0.9]]}},{"assetId":5,"instanceId":1,"fadeOutSeconds":5,"volume":{"type":"gps-distance-linear","latitude":25.042970651248297,"longtitude":121.52064993199508,"distanceVolumes":[[null,0.1],[30,0.3],[5,0.9]]}}]}}},{"id":3,"initial":{"display":{"type":"webview","assetId":6},"audio":{"tracks":[{"assetId":8}],"triggers":[{"type":"gps","latitude":25.043087166255518,"longitude":121.52001069414746,"distance":30,"actions":{"gotoScene":{"sceneId":4}}}]}}},{"id":4,"initial":{"display":{"type":"webview","assetId":9},"audio":{"tracks":[{"assetId":11}]}},"triggers":[{"type":"audioFinish","assetId":11,"actions":[{"fakeCall":{"instanceId":1,"callerName":"高高","callerNumber":"0914004566","ring":{"assetId":12,"repeat":15},"call":{"assetId":13}}}]},{"type":"audioFinish","assetId":13,"actions":{"gotoScene":{"sceneId":5}}},{"type":"fakeCallDeclined","instanceId":1,"actions":{"gotoScene":{"sceneId":4}}}]},{"id":5,"initial":{"notification":{"instanceId":1,"title":"你現在去前往立法院"}},"triggers":[{"type":"notificationClick","instanceId":1,"actions":{"gotoScene":{"sceneId":6}}}]},{"id":6,"initial":{"display":{"type":"webview","assetId":14}},"triggers":[{"type":"beacon","uuid":"b9d4fe7a-be80-46a0-9d76-5fe78f1b9405","major":1,"minor":1,"distance":20,"actions":{"gotoScene":{"sceneId":7}}},{"type":"timer","seconds":60,"actions":{"sceneAduio":{"tracks":[{"assetId":16}]}}},{"type":"timer","seconds":120,"actions":{"sceneAduio":{"tracks":[{"assetId":17}]}}},{"type":"timer","seconds":180,"actions":{"sceneAduio":{"tracks":[{"assetId":18}]}}},{"type":"audioFinish","assetId":18,"actions":{"gotoScene":{"sceneId":6}}}]},{"id":7,"initial":{"display":{"type":"webview","assetId":19}},"triggers":[{"type":"gps","latitude":25.043087166255518,"longitude":121.52001069414746,"distance":30,"actions":{"gotoScene":{"sceneId":8}}},{"type":"gps","latitude":25.543087166255518,"longitude":121.02001069414746,"distance":30,"actions":{"flag":{"name":"hitWrongPath"}}},{"type":"flagMatch","condition":"hitWrongPath !wrongFlagNoticePlayed","actions":{"flag":{"name":"wrongFlagNoticePlayed"},"audio":[{"assetId":21}]}}]},{"id":8,"initial":{"display":{"type":"webview","assetId":22}},"triggers":[{"type":"webviewScript","id":"noBento","actions":{"flag":{"name":"confirmNoBento"}}},{"type":"gps","latitude":25.043087166255518,"longitude":121.52001069414746,"distance":30,"actions":{"flag":{"name":"at_7-11"}}},{"type":"gps","latitude":25.043087166255518,"longitude":121.52001069414746,"distance":30,"operator":">","actions":{"unflag":{"name":"at_7-11"}}},{"type":"flagMatch","condition":"confirmNoBento at_7-11","actions":{"gotoScene":{"sceneId":9}}}]},{"id":9,"initial":{"audio":{"tracks":[{"assetId":23,"instanceId":1}]}},"triggers":[{"type":"timer","seconds":300,"actions":{"audio":{"tracks":[{"assetId":23,"instanceId":1,"pause":true,"fadeOutSeconds":10}]}}},{"type":"timer","seconds":320,"actions":{"audio":{"tracks":[{"assetId":23,"instanceId":1,"pause":false,"fadeInSeconds":10}]}}},{"type":"audioFinish","audioId":23,"instanceId":1,"actions":{"gotoScene":{"sceneId":10}}}]},{"id":10,"initial":{"display":{"type":"webview","assetId":24}},"triggers":[{"type":"webviewClick","id":"ok","actions":{"qrScan":{"instanceId":"location-password"}}},{"type":"qrcode","instanceId":"location-password","match":"my-password","actions":{"audio":{"tracks":[{"assetId":8}]},"changePlaybookThumbnail":{"assetId":25},"disablePlaybook":{"message":"劇本結束","retryMessage":"已經玩過了"},"finishPlaybook":true}}]}]
     * assets : [{"id":"23","sha256":"e604aa5f5949ad13800d9f373df88fcf043b111f33569ddc4d4f1d3a26fdc883","contentType":"audio/mpeg"},{"id":"8","sha256":"892df19cd12a7702320fed6090674c897fe390acf33c06848e0188864caeac7e","contentType":"audio/mpeg"},{"id":"20","sha256":"99fdea799cedcab4e48812e97114ef78cb2f3b69a7338387f8e311a378365004","contentType":"image/jpeg"},{"id":"2","sha256":"5dfc19201dc172830e8f83901d5358a4035e00cc4030285c00f2993ae1d80172","contentType":"image/jpeg"},{"id":"12","sha256":"e660df432b25a1c30621c341b35c32ea00887d49ff70cd88f096c3c7b7d0a422","contentType":"audio/mpeg"},{"id":"15","sha256":"435d1250b0be1ebfd978801ee43b2f96d76c340bddd0637384bc7c494699a136","contentType":"image/jpeg"},{"id":"17","sha256":"32d6806f4a851309d282abe90ee0c1910b31c35b9e9d8aa1743634da9fa0ae72","contentType":"audio/mpeg"},{"id":"6","sha256":"8b7baf8fc9edf73da40b924661198d9a69192879730efe8746706397fa9eeca0","contentType":"text/html"},{"id":"25","sha256":"0eb07197448bdcd6ffdc0a03b2bd10d450871df9bfa6c090a91d14fbe56aa3cd","contentType":"image/png"},{"id":"9","sha256":"5191a9c6d21f4bbda21530d29463fd8303ec2c25ed57c0d01b403c81fdf34183","contentType":"text/html"},{"id":"19","sha256":"47743ea21af1c0ab5f772acf27e3f7e34e03f7c04fe8662fc7c03959bda63aa8","contentType":"text/html"},{"id":"10","sha256":"18b18c524121fcfcb811eb990ab97b499e1ccac684e16542b3427436405ba020","contentType":"image/jpeg"},{"id":"4","sha256":"2f081cafe4d1a92413918a6a30fea396589fc75efe96b29e8e2364e64303a211","contentType":"audio/mpeg"},{"id":"24","sha256":"93a28318d09ac356a16a8333f6fd379d1215ada39723a689aacf2eacdeea879e","contentType":"text/html"},{"id":"13","sha256":"56e28d98269ad0e921d9aa04a7d8be99dc176306dca87956d4557713d3fa500e","contentType":"audio/mpeg"},{"id":"5","sha256":"ca035f5a1131c0cb05453ff3b6b4b3ddd8bb86b9bc71bbb1ef8d2d3414c7b6c0","contentType":"audio/mpeg"},{"id":"7","sha256":"53f4f74e94940fa8136a18c299b5a4f89036ede7effc7907005c3d893e7d1ea7","contentType":"image/jpeg"},{"id":"14","sha256":"d010446299fbd2ce18170b2fe1f4064434d84228d023007164fc70bd01bbfccf","contentType":"text/html"},{"id":"18","sha256":"93d8e13a5912327771473118065c229a36ef04dd3b11f34b6fb7991b80ac4042","contentType":"audio/mpeg"},{"id":"11","sha256":"4de9ca22d23ead31ecbe3acb62d6aab521648386e9c1211193fa5680f7b1d6b9","contentType":"audio/mpeg"},{"id":"22","sha256":"a39c3ba29c56a11105b00b08c9de7aa882e8aee263268ef487f385ca383da1f7","contentType":"text/html"},{"id":"21","sha256":"42dfcc69023132bda55d54da87c3bc8b097fe01af39c76c47f72f840aee6a04f","contentType":"video/mp4"},{"id":"16","sha256":"5a1b9b840dfc17d9d5c08d91706ea5e0e3aea0b1009dcb761d8ce98be7d7a9e3","contentType":"audio/mpeg"},{"id":"1","sha256":"76aae4d8507e7dc25275d475b1916b7ae150057ceae1a55e21d21572a565a7dd","contentType":"text/html"},{"id":"26","sha256":"480afb949434c4a7ad6bb5424cc2278e47f8ccf2fe5138880184d963290d7511","contentType":"image/png"},{"id":"3","sha256":"f97cfd08e08b58c7f9e60a658335f868c0e53bd418c17e2f3ec5f5024479b6d2","contentType":"audio/mpeg"}]
     */

    private int backgroundSceneId;
    private List<ScenesBean> scenes;
    private List<AssetsBean> assets;

    public int getBackgroundSceneId() {
        return backgroundSceneId;
    }

    public void setBackgroundSceneId(int backgroundSceneId) {
        this.backgroundSceneId = backgroundSceneId;
    }

    public List<ScenesBean> getScenes() {
        return scenes;
    }

    public void setScenes(List<ScenesBean> scenes) {
        this.scenes = scenes;
    }

    public List<AssetsBean> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetsBean> assets) {
        this.assets = assets;
    }

    public static class ScenesBean {
        /**
         * id : 2
         * initial : {"display":{"type":"webview","assetId":1}}
         * triggers : [{"type":"audioFinish","assetId":11,"actions":[{"fakeCall":{"instanceId":1,"callerName":"高高","callerNumber":"0914004566","ring":{"assetId":12,"repeat":15},"call":{"assetId":13}}}]},{"type":"audioFinish","assetId":13,"actions":{"gotoScene":{"sceneId":5}}},{"type":"fakeCallDeclined","instanceId":1,"actions":{"gotoScene":{"sceneId":4}}}]
         */

        private int id;
        private InitialBean initial;
        private List<TriggersBean> triggers;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public InitialBean getInitial() {
            return initial;
        }

        public void setInitial(InitialBean initial) {
            this.initial = initial;
        }

        public List<TriggersBean> getTriggers() {
            return triggers;
        }

        public void setTriggers(List<TriggersBean> triggers) {
            this.triggers = triggers;
        }

        public static class InitialBean {
            /**
             * display : {"type":"webview","assetId":1}
             */

            private DisplayBean display;

            public DisplayBean getDisplay() {
                return display;
            }

            public void setDisplay(DisplayBean display) {
                this.display = display;
            }

            public static class DisplayBean {
                /**
                 * type : webview
                 * assetId : 1
                 */

                private String type;
                private int assetId;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public int getAssetId() {
                    return assetId;
                }

                public void setAssetId(int assetId) {
                    this.assetId = assetId;
                }
            }
        }

        public static class TriggersBean {
            /**
             * type : audioFinish
             * assetId : 11
             * actions : [{"fakeCall":{"instanceId":1,"callerName":"高高","callerNumber":"0914004566","ring":{"assetId":12,"repeat":15},"call":{"assetId":13}}}]
             * instanceId : 1
             */

            private String type;
            private int assetId;
            private int instanceId;
            private List<ActionsBean> actions;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getAssetId() {
                return assetId;
            }

            public void setAssetId(int assetId) {
                this.assetId = assetId;
            }

            public int getInstanceId() {
                return instanceId;
            }

            public void setInstanceId(int instanceId) {
                this.instanceId = instanceId;
            }

            public List<ActionsBean> getActions() {
                return actions;
            }

            public void setActions(List<ActionsBean> actions) {
                this.actions = actions;
            }

            public static class ActionsBean {
                /**
                 * fakeCall : {"instanceId":1,"callerName":"高高","callerNumber":"0914004566","ring":{"assetId":12,"repeat":15},"call":{"assetId":13}}
                 */

                private FakeCallBean fakeCall;

                public FakeCallBean getFakeCall() {
                    return fakeCall;
                }

                public void setFakeCall(FakeCallBean fakeCall) {
                    this.fakeCall = fakeCall;
                }

                public static class FakeCallBean {
                    /**
                     * instanceId : 1
                     * callerName : 高高
                     * callerNumber : 0914004566
                     * ring : {"assetId":12,"repeat":15}
                     * call : {"assetId":13}
                     */

                    private int instanceId;
                    private String callerName;
                    private String callerNumber;
                    private RingBean ring;
                    private CallBean call;

                    public int getInstanceId() {
                        return instanceId;
                    }

                    public void setInstanceId(int instanceId) {
                        this.instanceId = instanceId;
                    }

                    public String getCallerName() {
                        return callerName;
                    }

                    public void setCallerName(String callerName) {
                        this.callerName = callerName;
                    }

                    public String getCallerNumber() {
                        return callerNumber;
                    }

                    public void setCallerNumber(String callerNumber) {
                        this.callerNumber = callerNumber;
                    }

                    public RingBean getRing() {
                        return ring;
                    }

                    public void setRing(RingBean ring) {
                        this.ring = ring;
                    }

                    public CallBean getCall() {
                        return call;
                    }

                    public void setCall(CallBean call) {
                        this.call = call;
                    }

                    public static class RingBean {
                        /**
                         * assetId : 12
                         * repeat : 15
                         */

                        private int assetId;
                        private int repeat;

                        public int getAssetId() {
                            return assetId;
                        }

                        public void setAssetId(int assetId) {
                            this.assetId = assetId;
                        }

                        public int getRepeat() {
                            return repeat;
                        }

                        public void setRepeat(int repeat) {
                            this.repeat = repeat;
                        }
                    }

                    public static class CallBean {
                        /**
                         * assetId : 13
                         */

                        private int assetId;

                        public int getAssetId() {
                            return assetId;
                        }

                        public void setAssetId(int assetId) {
                            this.assetId = assetId;
                        }
                    }
                }
            }
        }
    }

    public static class AssetsBean {
        /**
         * id : 23
         * sha256 : e604aa5f5949ad13800d9f373df88fcf043b111f33569ddc4d4f1d3a26fdc883
         * contentType : audio/mpeg
         */

        private String id;
        private String sha256;
        private String contentType;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSha256() {
            return sha256;
        }

        public void setSha256(String sha256) {
            this.sha256 = sha256;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }
}
