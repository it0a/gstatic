import groovy.transform.CompileStatic

class GStatic {

    static main(args) {
        new GStatic().run()
    }

    @CompileStatic
    def loadData() {
        [
            [
                id: 1,
                children: [
                    [id: 3, type: "B"],
                    [id: 4, type: "B"],
                    [id: 5, type: "B"]
                ]
            ],
            [
                id: 2,
                children: [
                    [id: 6, type: "B"],
                    [id: 7, type: "B"],
                    [id: 8, type: "B"]
                ]
            ],
        ]
    }

    def run() {
        println loadData()
    }

}
