class Parameters {

    constructor(config) {
        this.config = config;
        this.parameterList = [];
    }

    async fetch(url) {
        const response = await fetch(url);
        this.parameterList = await response.json();
    }

    async update() {
        await this.fetch(this.config.URL_PARAMS);
    }

    getParameterNumbers() {
        const parameterNumbers = [];
        for (const parameter of this.parameterList) {
            parameterNumbers.push(parameter.elem_no);
        }
        return parameterNumbers;
    }

    getParameterList() {
        return this.parameterList;
    }

    getParameter(elemNo) {
        for (const parameter of this.parameterList) {
            if (parameter.elem_no == elemNo) return parameter;
        }
        return undefined;
    }

    clear() {
        this.parameterList = [];
    }

    add(parameter) {
        this.parameterList.push(parameter);
    }

}