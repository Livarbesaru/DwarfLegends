let data = null;

async function fetchData(){
    if(!data){
        data = await fetch("/fetch/base")
            .then(response=>response.json()).catch(err=>null);
    }
}

fetchData();