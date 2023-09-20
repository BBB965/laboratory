//// 점을 생성해서, 점들을 이어주는 선, 점을 위아래로 움직여주면 웨이브처럼 보이게 된다
/// index : 현재 point 몇번째인지 같이 알려줌
export class Point {
    constructor(index, x, y) {
        this.x = x;
        this.y = y;
        this.fixedY = y;
        this.speed = 0.05;
        this.cur = index;
        this.max = Math.random() * 100 + 150;
    }

    update() {
        this.cur += this.speed;
        this.y = this.fixedY + (Math.sin(this.cur) * this.max);
    }
}